package com.ureca.picky_be.base.business.auth.dto;

public record OAuth2Token(
        String accessToken,
        String refreshToken,
        String tokenType,
        String expiresIn
) {}
