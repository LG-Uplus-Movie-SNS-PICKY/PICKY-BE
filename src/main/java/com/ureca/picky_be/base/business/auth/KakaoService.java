package com.ureca.picky_be.base.business.auth;

import com.ureca.picky_be.base.business.auth.dto.DeleteUserReq;
import com.ureca.picky_be.base.business.auth.dto.LoginUserInfo;
import com.ureca.picky_be.base.business.auth.dto.OAuth2Token;
import com.ureca.picky_be.base.implementation.auth.AuthManager;
import com.ureca.picky_be.base.implementation.auth.KakaoManager;
import com.ureca.picky_be.global.success.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KakaoService implements OAuth2UseCase {

    private final KakaoManager kakaoManager;
    private final AuthManager authManager;

    //TODO: state 랜덤 생성 및 유효성 검사
    private String state="1234";

/*    @Override
    public LoginUrlResp getLoginUrl() {
        return kakaoManager.buildCodeUrl(state);
    }*/

    @Override
    public SuccessCode sendJwtToken(String state, String code) {
        OAuth2Token oAuth2Token = kakaoManager.getOAuth2Token(code);
        String email = kakaoManager.getUserInfo(oAuth2Token.accessToken());
        LoginUserInfo loginUserInfo = kakaoManager.getLocalJwt(email, oAuth2Token.accessToken());
        System.out.println(loginUserInfo);
        System.out.println(oAuth2Token);

        return kakaoManager.sendResponseToFrontend(oAuth2Token, loginUserInfo.jwt(), kakaoManager.isRegistrationDone(authManager.getUserId()));
    }

    @Override
    public SuccessCode deleteAccount(DeleteUserReq req) {
        return kakaoManager.deleteAccount(authManager.getUserId(), req);
    }
}
