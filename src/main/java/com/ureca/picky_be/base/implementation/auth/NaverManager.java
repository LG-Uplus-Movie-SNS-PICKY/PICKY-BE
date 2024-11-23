package com.ureca.picky_be.base.implementation.auth;

import com.ureca.picky_be.base.business.auth.dto.LoginUserResp;
import com.ureca.picky_be.base.business.auth.dto.OAuth2Token;
import com.ureca.picky_be.base.persistence.UserRepository;
import com.ureca.picky_be.base.presentation.web.JwtTokenProvider;
import com.ureca.picky_be.base.presentation.web.LocalJwtDto;
import com.ureca.picky_be.config.oAuth2.NaverConfig;
import com.ureca.picky_be.jpa.user.Platform;
import com.ureca.picky_be.jpa.user.Role;
import com.ureca.picky_be.jpa.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

@RequiredArgsConstructor
@Component
public class NaverManager {

    private final NaverConfig naverConfig;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    RestClient restClient = RestClient.create();

    public String buildCodeUrl(String state){
        return  UriComponentsBuilder
                .fromHttpUrl(naverConfig.getCodeUrl())
                .queryParam("client_id", naverConfig.getClientId())
                .queryParam("client_secret", naverConfig.getClientSecret())
                .queryParam("response_type", "code")
                .queryParam("redirect_uri", naverConfig.getRedirectUri())
                .queryParam("state", state)
                .build()
                .toUriString();
    }

    public OAuth2Token getAccessToken(String state, String code){
        try {
            return restClient
                    .get()
                    .uri(buildTokenUrl(state, code))
                    .retrieve()
                    .toEntity(OAuth2Token.class)
                    .getBody();
        } catch (RestClientResponseException e) {
            throw new IllegalStateException("네이버 token 요청 호출을 실패했습니다: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("네이버 token 요청 중 예외가 발생했습니다.", e);
        }
    }

    private String buildTokenUrl(String state, String code){
        return UriComponentsBuilder
                .fromHttpUrl(naverConfig.getCodeUrl())
                .queryParam("client_id", naverConfig.getClientId())
                .queryParam("client_secret", naverConfig.getClientSecret())
                .queryParam("grant_type", "authorization_code")
                .queryParam("state", state)
                .queryParam("code", code)
                .build().toUriString();
    }

    public LocalJwtDto getLocalJwt(String accessToken) {
        LoginUserResp resp = getUserInfo(accessToken);

        User user = userRepository.findByEmailAndPlatform(resp.email(), Platform.NAVER)
                .orElseGet(() -> createNewUser(resp.email()));

        return jwtTokenProvider.generate(user.getId(), user.getRole().toString());
    }

    private User createNewUser(String email) {
        User newUser = User.builder()
                .platform(Platform.NAVER)
                .role(Role.USER)
                .email(email)
                .build();
        return userRepository.save(newUser);
    }

    private LoginUserResp getUserInfo(String accessToken) {
        try {
            return restClient
                    .get()
                    .uri(buildInfoUrl())
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .toEntity(LoginUserResp.class)
                    .getBody();
        } catch (RestClientResponseException e) {
            throw new IllegalStateException("네이버 유저 정보 요청 호출을 실피했습니다: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("네이버 유저 정보 요청 중 예외가 발생했습니다.", e);
        }
    }

    private String buildInfoUrl(){
        return UriComponentsBuilder
                .fromHttpUrl(naverConfig.getInfoUrl())
                .build()
                .toUriString();
    }



}
