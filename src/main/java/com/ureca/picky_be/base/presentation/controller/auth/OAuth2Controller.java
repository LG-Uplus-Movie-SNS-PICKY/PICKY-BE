package com.ureca.picky_be.base.presentation.controller.auth;

import com.ureca.picky_be.base.business.auth.OAuth2UseCase;
import com.ureca.picky_be.base.business.auth.OAuth2UseCaseResolver;
import com.ureca.picky_be.base.business.auth.dto.DeleteUserReq;
import com.ureca.picky_be.base.business.auth.dto.LoginUrlResp;
import com.ureca.picky_be.base.business.auth.dto.OAuth2Token;
import com.ureca.picky_be.global.success.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/oauth")
public class OAuth2Controller {
    private final OAuth2UseCaseResolver oAuth2UseCaseResolver;

    @GetMapping("/{platform}/login")
    public LoginUrlResp getOAuth2LoginUrl (@PathVariable String platform) {
        OAuth2UseCase oAuth2UseCase = oAuth2UseCaseResolver.resolve(platform);
        return oAuth2UseCase.getLoginUrl();
    }

    @GetMapping("/{platform}/user")
    public SuccessCode getUserInfo(@PathVariable String platform,
                                   @RequestParam String code,
                                   @RequestParam String state
                                            ){
        OAuth2UseCase oAuth2UseCase = oAuth2UseCaseResolver.resolve(platform);
        return oAuth2UseCase.getUserInfo(state, code);
    }

    @DeleteMapping("/{platform}/user")
    public SuccessCode deleteUser(@PathVariable String platform,
                                  @RequestBody DeleteUserReq req) {
        OAuth2UseCase oAuth2UseCase = oAuth2UseCaseResolver.resolve(platform);
        return oAuth2UseCase.deleteAccount(req);
    }
}