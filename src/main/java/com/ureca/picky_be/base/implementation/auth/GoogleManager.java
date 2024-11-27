package com.ureca.picky_be.base.implementation.auth;

import com.ureca.picky_be.base.business.auth.dto.DeleteUserReq;
import com.ureca.picky_be.base.business.auth.dto.LoginUrlResp;
import com.ureca.picky_be.base.business.auth.dto.LoginUserResp;
import com.ureca.picky_be.base.business.auth.dto.OAuth2Token;
import com.ureca.picky_be.base.persistence.UserRepository;
import com.ureca.picky_be.global.exception.CustomException;
import com.ureca.picky_be.global.exception.ErrorCode;
import com.ureca.picky_be.global.success.SuccessCode;
import com.ureca.picky_be.global.web.JwtTokenProvider;
import com.ureca.picky_be.global.web.LocalJwtDto;
import com.ureca.picky_be.jpa.user.Role;
import com.ureca.picky_be.jpa.user.SocialPlatform;
import com.ureca.picky_be.jpa.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import com.ureca.picky_be.config.oAuth2.GoogleConfig;

import java.util.Map;

@RequiredArgsConstructor
@Component
public class GoogleManager {

    private final GoogleConfig googleConfig;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    RestClient restClient = RestClient.create();

    public LoginUrlResp buildCodeUrl(String state){
        return new LoginUrlResp(UriComponentsBuilder
                .fromHttpUrl(googleConfig.getCodeUrl())
                .queryParam("client_id", googleConfig.getClientId())
                .queryParam("response_type", "code")
                .queryParam("redirect_uri", googleConfig.getRedirectUri())
                .queryParam("scope", "email")
                .queryParam("state", state)
                .build()
                .toUriString());
    }

    public OAuth2Token getOAuth2Token(String code){
        try{
            return restClient
                    .post()
                    .uri(buildTokenUrl(code))
                    .retrieve()
                    .toEntity(OAuth2Token.class)
                    .getBody();
        } catch (RestClientResponseException e) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private String buildTokenUrl(String code){
        return UriComponentsBuilder
                .fromHttpUrl(googleConfig.getTokenUrl())
                .queryParam("client_id", googleConfig.getClientId())
                .queryParam("client_secret", googleConfig.getClientSecret())
                .queryParam("code", code)
                .queryParam("grant_type", "authorization_code")
                .queryParam("redirect_uri", googleConfig.getRedirectUri())
                .build()
                .toUriString();
    }

    public String getUserInfo(String accessToken){
        try {
            Map response = restClient
                                .get()
                                .uri(buildInfoUrl())
                                .header("Authorization", "Bearer " + accessToken)
                                .retrieve()
                                .body(Map.class);
            return (String) response.get("email");
        } catch (RestClientResponseException e) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private String buildInfoUrl(){
        return UriComponentsBuilder
                .fromHttpUrl(googleConfig.getInfoUrl())
                .build()
                .toUriString();
    }

    public LocalJwtDto getLocalJwt(String email, String accessToken) {
        try{
            User user = userRepository.findByEmailAndSocialPlatform(email, SocialPlatform.GOOGLE)
                    .orElseGet(() -> createNewUser(email));
            return jwtTokenProvider.generate(user.getId(), user.getRole().toString());
        } catch (Exception e){
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
    }

    private User createNewUser(String email) {
        try{
            User newUser = User.builder()
                    .socialPlatform(SocialPlatform.GOOGLE)
                    .role(Role.USER)
                    .email(email)
                    .build();
            return userRepository.save(newUser);
        } catch (Exception e){
            throw new CustomException(ErrorCode.USER_SAVE_FAILED);
        }
    }

    public SuccessCode sendResponseToFrontend(OAuth2Token oAuth2Token, String email, LocalJwtDto jwt) {
        LoginUserResp resp = new LoginUserResp(oAuth2Token, email, jwt);
        try {
            restClient
                    .post()
                    .uri(buildFrontendUrl())
                    .header("Content-Type", "application/json")
                    .body(resp)
                    .retrieve()
                    .toBodilessEntity();
            return SuccessCode.REQUEST_FRONT_SUCCESS;
        } catch (RestClientResponseException e) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private String buildFrontendUrl(){
        return UriComponentsBuilder
                .fromHttpUrl(googleConfig.getFrontendServer())
                .build()
                .toUriString();
    }

    @Transactional
    public SuccessCode deleteAccount(DeleteUserReq req) {
        restClient
                .post()
                .uri(buildDeleteUrl(req.oAuth2Token().accessToken()))
                .header("Content-Type", "application/json")
                .retrieve()
                .toBodilessEntity();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!authentication.isAuthenticated()) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        Long userId = Long.parseLong(authentication.getName());

        if (!userRepository.existsById(userId)) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        userRepository.deleteById(userId);
        return SuccessCode.REQUEST_DELETE_ACCOUNT_SUCCESS;
    }

    private String buildDeleteUrl(String accessToken){
        return UriComponentsBuilder
                .fromHttpUrl(googleConfig.getDeleteUrl())
                .queryParam("token", accessToken)
                .build()
                .toUriString();
    }
}
