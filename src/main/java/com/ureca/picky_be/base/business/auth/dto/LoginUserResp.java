package com.ureca.picky_be.base.business.auth.dto;

import com.ureca.picky_be.global.web.LocalJwtDto;

public record LoginUserResp (OAuth2Token oAuth2Token, String email, LocalJwtDto localJwtDto) {
}
