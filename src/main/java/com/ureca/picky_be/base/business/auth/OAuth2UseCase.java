package com.ureca.picky_be.base.business.auth;

import com.ureca.picky_be.base.business.auth.dto.LoginUserReq;
import com.ureca.picky_be.base.presentation.web.LocalJwtDto;

public interface OAuth2UseCase {

    String getLoginUrl();
    LocalJwtDto getLoginUserToken(LoginUserReq loginUserReq);
}
