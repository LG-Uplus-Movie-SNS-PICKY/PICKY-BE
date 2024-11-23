package com.ureca.picky_be.config.oAuth2;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class NaverConfig {

    @Value("${naver.client_id}")
    private String clientId;

    @Value("${naver.client_secret}")
    private String clientSecret;

    @Value("${naver.redirect_uri}")
    private String redirectUri;

    @Value("${naver.code_url}")
    private String codeUrl;

    @Value("${naver.token_url}")
    private String tokenUrl;

    @Value("${naver.info_url}")
    private String infoUrl;

}
