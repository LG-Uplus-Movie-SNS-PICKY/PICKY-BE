package com.ureca.picky_be.base.business.auth;


import com.ureca.picky_be.base.business.auth.dto.DeleteUserReq;
import com.ureca.picky_be.base.business.auth.dto.LoginUserInfo;
import com.ureca.picky_be.base.business.auth.dto.OAuth2Token;
import com.ureca.picky_be.base.implementation.auth.AuthManager;
import com.ureca.picky_be.base.implementation.auth.NaverManager;
import com.ureca.picky_be.global.success.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class NaverService implements OAuth2UseCase{

    private final NaverManager naverManager;
    private final AuthManager authManager;

    //TODO: state 랜덤 생성 및 유효성 검사
    private String state="1234";

/*    @Override
    public LoginUrlResp getLoginUrl(){
        return naverManager.buildCodeUrl(state);
    }*/

    @Override
    public SuccessCode sendJwtToken(String state, String code) {
        OAuth2Token oAuth2Token = naverManager.getOAuth2Token(state, code);
        String email = naverManager.getUserInfo(oAuth2Token.accessToken());
        LoginUserInfo loginUserInfo = naverManager.getLocalJwt(email);
        System.out.println(loginUserInfo);
        System.out.println(oAuth2Token);

        return naverManager.sendResponseToFrontend(oAuth2Token, loginUserInfo.jwt(), naverManager.isRegistrationDone(authManager.getUserId()));
    }

    @Override
    public SuccessCode deleteAccount(DeleteUserReq req) {
        return naverManager.deleteAccount(authManager.getUserId(), req);
    }
}
