package com.ureca.picky_be.config.oAuth2;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class KakaoConfig {

    @Value("${kakao.client_id}")
    private String clientId;

    @Value("${kakao.client_secret}")
    private String clientSecret;

    @Value("${kakao.redirect_uri}")
    private String redirectUri;

    @Value("${kakao.code_url}")
    private String codeUrl;

    @Value("${kakao.token_url}")
    private String tokenUrl;

    @Value("${kakao.info_url}")
    private String infoUrl;

    @Value("${kakao.delete_url}")
    private String deleteUrl;

    @Value("${frontend.server}")
    private String frontendServer;

}
