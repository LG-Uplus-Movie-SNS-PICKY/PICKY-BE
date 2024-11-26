package com.ureca.picky_be.base.business.auth;

import com.ureca.picky_be.base.business.auth.dto.DeleteUserReq;
import com.ureca.picky_be.base.business.auth.dto.OAuth2Token;
import com.ureca.picky_be.base.implementation.auth.KakaoManager;
import com.ureca.picky_be.base.presentation.web.LocalJwtDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KakaoService implements OAuth2UseCase {

    private final KakaoManager kakaoManager;

    //TODO: state 랜덤 생성 및 유효성 검사
    private String state="1234";

    @Override
    public String getLoginUrl() {
        return kakaoManager.buildCodeUrl(state);
    }

    @Override
    public String getUserInfo(String state, String code) {
        OAuth2Token oAuth2Token = kakaoManager.getOAuth2Token(code);
        String email = kakaoManager.getUserInfo(oAuth2Token.accessToken());
        LocalJwtDto jwt = kakaoManager.getLocalJwt(email, oAuth2Token.accessToken());
        return kakaoManager.sendResponseToFrontend(oAuth2Token, email, jwt);
    }

    @Override
    public String deleteAccount(DeleteUserReq req) {
        return kakaoManager.deleteAccount(req.jwt(), req.oAuth2Token());
    }


}
