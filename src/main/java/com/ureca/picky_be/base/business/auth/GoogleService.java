package com.ureca.picky_be.base.business.auth;

import com.ureca.picky_be.base.business.auth.dto.OAuth2Token;
import com.ureca.picky_be.base.implementation.auth.GoogleManager;
import com.ureca.picky_be.base.presentation.web.LocalJwtDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GoogleService implements OAuth2UseCase{

    private final GoogleManager googleManager;

    //TODO: state 랜덤 생성 및 유효성 검사
    private String state="1234";

    @Override
    public String getLoginUrl() {
        return googleManager.buildCodeUrl(state);
    }

    @Override
    public String getUserInfo(String state, String code) {
        OAuth2Token oAuth2Token = googleManager.getOAuth2Token(state, code);
        String email = googleManager.getUserInfo(oAuth2Token.accessToken());
        LocalJwtDto jwt = googleManager.getLocalJwt(email, oAuth2Token.accessToken());
        return googleManager.sendResponseToFrontend(oAuth2Token, email, jwt);
    }
}
