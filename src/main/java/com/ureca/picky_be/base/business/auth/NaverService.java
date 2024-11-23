package com.ureca.picky_be.base.business.auth;


import com.ureca.picky_be.base.business.auth.dto.LoginUserReq;
import com.ureca.picky_be.base.business.auth.dto.OAuth2Token;
import com.ureca.picky_be.base.implementation.auth.NaverManager;
import com.ureca.picky_be.base.presentation.web.LocalJwtDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NaverService implements OAuth2UseCase{

    private final NaverManager naverManager;

    //TODO: state 랜덤 생성 및 유효성 검사
    private String state="1234";

    @Override
    public String getLoginUrl(){
        return naverManager.buildCodeUrl(state);
    }

    @Override
    public LocalJwtDto getLoginUserToken(LoginUserReq req) {
        //TODO: 네이버 token은 어디에 저장할지 논의 (추후에 토큰 갱신이나 정보 다시 받을 일 있으면 필요함)
        OAuth2Token oAuth2Token = naverManager.getAccessToken(state, req.code());
        return naverManager.getLocalJwt(oAuth2Token.accessToken());
    }
}
