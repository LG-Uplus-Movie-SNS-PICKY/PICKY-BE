package com.ureca.picky_be.base.presentation.controller.auth;

import com.ureca.picky_be.base.business.auth.OAuth2UseCaseResolver;
import com.ureca.picky_be.base.business.auth.Platform;
import com.ureca.picky_be.base.business.auth.dto.LoginUserReq;
import org.springframework.web.bind.annotation.*;

import com.ureca.picky_be.base.business.auth.OAuth2UseCase;
import com.ureca.picky_be.base.presentation.web.LocalJwtDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/oauth")
public class OAuth2Controller {
    private final OAuth2UseCaseResolver oAuth2UseCaseResolver;

    @GetMapping("/naver/login")
    public ResponseEntity<String> getNaverLoginUrl(){
        OAuth2UseCase oAuth2UseCase = oAuth2UseCaseResolver.resolve(Platform.NAVER.toString());
        return ResponseEntity.ok(oAuth2UseCase.getLoginUrl());
    }

    @PostMapping("/naver/user")
    public ResponseEntity<LocalJwtDto> getNaverUserToken(@RequestBody LoginUserReq req){
        OAuth2UseCase oAuth2UseCase = oAuth2UseCaseResolver.resolve(Platform.NAVER.toString());
        return ResponseEntity.ok(oAuth2UseCase.getLoginUserToken(req));
    }
}
