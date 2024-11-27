package com.ureca.picky_be.base.business.auth;

import com.ureca.picky_be.base.business.auth.dto.DeleteUserReq;
import com.ureca.picky_be.base.business.auth.dto.LoginUrlResp;
import com.ureca.picky_be.global.success.SuccessCode;

public interface OAuth2UseCase {

    LoginUrlResp getLoginUrl();
    SuccessCode getUserInfo(String state, String code);
    SuccessCode deleteAccount(DeleteUserReq req);
}
