package com.ureca.picky_be.base.business.auth;

public interface OAuth2UseCase {

    String getLoginUrl();
    String getUserInfo(String state, String code);
}
