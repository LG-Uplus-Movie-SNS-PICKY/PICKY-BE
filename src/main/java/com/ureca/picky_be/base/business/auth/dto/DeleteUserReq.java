package com.ureca.picky_be.base.business.auth.dto;

public record DeleteUserReq(String jwt,
                            OAuth2Token oAuth2Token) {
}
