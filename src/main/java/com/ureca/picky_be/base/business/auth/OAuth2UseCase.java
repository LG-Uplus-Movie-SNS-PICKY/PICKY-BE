package com.ureca.picky_be.base.business.auth;

import com.ureca.picky_be.base.business.auth.dto.DeleteUserReq;
import com.ureca.picky_be.base.business.auth.dto.OAuth2Token;

public interface OAuth2UseCase {

    String getLoginUrl();
    String getUserInfo(String state, String code);
    String deleteAccount(DeleteUserReq req);
}
