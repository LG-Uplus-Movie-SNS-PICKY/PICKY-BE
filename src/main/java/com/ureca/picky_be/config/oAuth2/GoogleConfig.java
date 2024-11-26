package com.ureca.picky_be.config.oAuth2;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class GoogleConfig {
    @Value("${google.client_id}")
    private String clientId;

    @Value("${google.client_secret}")
    private String clientSecret;

    @Value("${google.redirect_uri}")
    private String redirectUri;

    @Value("${google.code_url}")
    private String codeUrl;

    @Value("${google.token_url}")
    private String tokenUrl;

    @Value("${google.info_url}")
    private String infoUrl;

    @Value("${google.delete_url}")
    private String deleteUrl;

    @Value("${frontend.server}")
    private String frontendServer;
}
