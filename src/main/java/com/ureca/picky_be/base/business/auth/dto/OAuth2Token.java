package com.ureca.picky_be.base.business.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OAuth2Token(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("refresh_token") String refreshToken,
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("expires_in") String expiresIn
) {}
