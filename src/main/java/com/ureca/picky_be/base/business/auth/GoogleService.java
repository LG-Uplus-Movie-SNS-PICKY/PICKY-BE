package com.ureca.picky_be.base.business.auth;

import com.ureca.picky_be.base.business.auth.dto.DeleteUserReq;
import com.ureca.picky_be.base.business.auth.dto.LoginUrlResp;
import com.ureca.picky_be.base.business.auth.dto.OAuth2Token;
import com.ureca.picky_be.base.implementation.auth.GoogleManager;
import com.ureca.picky_be.global.success.SuccessCode;
import com.ureca.picky_be.global.web.LocalJwtDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GoogleService implements OAuth2UseCase{

    private final GoogleManager googleManager;

    //TODO: state 랜덤 생성 및 유효성 검사
    private String state="1234";

    @Override
    public LoginUrlResp getLoginUrl() {
        return googleManager.buildCodeUrl(state);
    }

    @Override
    public SuccessCode getUserInfo(String state, String code) {
        OAuth2Token oAuth2Token = googleManager.getOAuth2Token(code);
        String email = googleManager.getUserInfo(oAuth2Token.accessToken());
        LocalJwtDto jwt = googleManager.getLocalJwt(email, oAuth2Token.accessToken());
        System.out.println(jwt);
        System.out.println(oAuth2Token);
        return googleManager.sendResponseToFrontend(oAuth2Token, email, jwt);
    }

    @Override
    public SuccessCode deleteAccount(DeleteUserReq req) {
        return googleManager.deleteAccount(req);
    }
}