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
import com.ureca.picky_be.config.oAuth2.NaverConfig;
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

import java.util.Map;

@RequiredArgsConstructor
@Component
public class NaverManager {

    private final NaverConfig naverConfig;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    private final RestClient restClient = RestClient.create();

    public LoginUrlResp buildCodeUrl(String state){
        return new LoginUrlResp(UriComponentsBuilder
                .fromHttpUrl(naverConfig.getCodeUrl())
                .queryParam("client_id", naverConfig.getClientId())
                .queryParam("client_secret", naverConfig.getClientSecret())
                .queryParam("response_type", "code")
                .queryParam("redirect_uri", naverConfig.getRedirectUri())
                .queryParam("state", state)
                .build()
                .toUriString());
    }

    public OAuth2Token getOAuth2Token(String state, String code){
        try {
            return restClient
                    .get()
                    .uri(buildTokenUrl(state, code))
                    .retrieve()
                    .toEntity(OAuth2Token.class)
                    .getBody();
        } catch (RestClientResponseException e) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private String buildTokenUrl(String state, String code){
        return UriComponentsBuilder
                .fromHttpUrl(naverConfig.getTokenUrl())
                .queryParam("client_id", naverConfig.getClientId())
                .queryParam("client_secret", naverConfig.getClientSecret())
                .queryParam("grant_type", "authorization_code")
                .queryParam("state", state)
                .queryParam("code", code)
                .build().toUriString();
    }

    public String getUserInfo(String accessToken) {
        try {
            Map response = restClient
                    .get()
                    .uri(buildInfoUrl())
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .body(Map.class);
            return (String) ((Map) response.get("response")).get("email");
        } catch (RestClientResponseException e) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private String buildInfoUrl(){
        return UriComponentsBuilder
                .fromHttpUrl(naverConfig.getInfoUrl())
                .build()
                .toUriString();
    }

    public LocalJwtDto getLocalJwt(String email) {
        try{
            User user = userRepository.findByEmailAndSocialPlatform(email, SocialPlatform.NAVER)
                    .orElseGet(() -> createNewUser(email));
            return jwtTokenProvider.generate(user.getId(), user.getRole().toString());
        } catch (Exception e){
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
    }

    private User createNewUser(String email) {
        try{
            User newUser = User.builder()
                    .socialPlatform(SocialPlatform.NAVER)
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
                .fromHttpUrl(naverConfig.getFrontendServer())
                .build()
                .toUriString();
    }

    @Transactional
    public SuccessCode deleteAccount(DeleteUserReq req) {
        restClient
                .get()
                .uri(buildDeleteUrl(req.oAuth2Token().accessToken()))
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
                .fromHttpUrl(naverConfig.getTokenUrl())
                .queryParam("client_id", naverConfig.getClientId())
                .queryParam("client_secret", naverConfig.getClientSecret())
                .queryParam("grant_type", "delete")
                .queryParam("access_token", accessToken)
                .build()
                .toUriString();
    }
}