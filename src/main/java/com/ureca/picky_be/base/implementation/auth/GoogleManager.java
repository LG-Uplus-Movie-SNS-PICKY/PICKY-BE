package com.ureca.picky_be.base.implementation.auth;

import com.ureca.picky_be.base.business.auth.dto.LoginUserResp;
import com.ureca.picky_be.base.business.auth.dto.OAuth2Token;
import com.ureca.picky_be.base.persistence.UserRepository;
import com.ureca.picky_be.base.presentation.web.JwtTokenProvider;
import com.ureca.picky_be.base.presentation.web.LocalJwtDto;
import com.ureca.picky_be.config.oAuth2.GoogleConfig;
import com.ureca.picky_be.jpa.user.Role;
import com.ureca.picky_be.jpa.user.SocialPlatform;
import com.ureca.picky_be.jpa.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@RequiredArgsConstructor
@Component
public class GoogleManager {

    private final GoogleConfig googleConfig;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    RestClient restClient = RestClient.create();

    public String buildCodeUrl(String state){
        return  UriComponentsBuilder
                .fromHttpUrl(googleConfig.getCodeUrl())
                .queryParam("client_id", googleConfig.getClientId())
                .queryParam("response_type", "code")
                .queryParam("redirect_uri", googleConfig.getRedirectUri())
                .queryParam("scope", "email")
                .queryParam("state", state)
                .build()
                .toUriString();
    }

    public OAuth2Token getOAuth2Token(String state, String code){
        try{
            return restClient
                    .post()
                    .uri(buildTokenUrl(state, code))
                    .retrieve()
                    .toEntity(OAuth2Token.class)
                    .getBody();
        } catch (RestClientResponseException e) {
            throw new IllegalStateException("네이버 token 요청 호출을 실패했습니다: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("구글 token 요청 중 예외가 발생했습니다.", e);
        }
    }

    private String buildTokenUrl(String state, String code){
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
            throw new IllegalStateException("구글 유저 정보 요청 호출을 실패했습니다: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("구글 유저 정보 요청 중 예외가 발생했습니다.", e);
        }
    }

    private String buildInfoUrl(){
        return UriComponentsBuilder
                .fromHttpUrl(googleConfig.getInfoUrl())
                .build()
                .toUriString();
    }

    public LocalJwtDto getLocalJwt(String email, String accessToken) {
        User user = userRepository.findByEmailAndSocialPlatform(email, SocialPlatform.GOOGLE)
                .orElseGet(() -> createNewUser(email));

        return jwtTokenProvider.generate(user.getId(), user.getRole().toString());
    }

    private User createNewUser(String email) {
        User newUser = User.builder()
                .socialPlatform(SocialPlatform.GOOGLE)
                .role(Role.USER)
                .email(email)
                .build();
        return userRepository.save(newUser);
    }

    public String sendResponseToFrontend(OAuth2Token oAuth2Token, String email, LocalJwtDto jwt) {
        LoginUserResp resp = new LoginUserResp(oAuth2Token, email, jwt);
        try {
            restClient
                    .post()
                    .uri(buildFrontendUrl())
                    .header("Content-Type", "application/json")
                    .body(resp)
                    .retrieve()
                    .toBodilessEntity();
            return "프론트엔드로 성공적으로 반환";
        } catch (RestClientResponseException e) {
            return "프론트엔드로 POST 요청 실패: " + e.getMessage();
        } catch (Exception e) {
            return "프론트엔드로 POST 요청 중 예외 발생: " + e.getMessage();
        }
    }

    private String buildFrontendUrl(){
        return UriComponentsBuilder
                .fromHttpUrl(googleConfig.getFrontendServer())
                .build()
                .toUriString();
    }
}
