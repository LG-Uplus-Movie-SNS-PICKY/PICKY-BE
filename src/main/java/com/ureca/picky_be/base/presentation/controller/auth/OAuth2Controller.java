package com.ureca.picky_be.base.presentation.controller.auth;

import com.ureca.picky_be.base.business.auth.OAuth2UseCase;
import com.ureca.picky_be.base.business.auth.OAuth2UseCaseResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/oauth")
public class OAuth2Controller {
    private final OAuth2UseCaseResolver oAuth2UseCaseResolver;

    @GetMapping("/{platform}/login")
    public ResponseEntity<String> getOAuth2LoginUrl (@PathVariable String platform) {
        OAuth2UseCase oAuth2UseCase = oAuth2UseCaseResolver.resolve(platform);
        return ResponseEntity.ok(oAuth2UseCase.getLoginUrl());
    }

    @GetMapping("/{platform}/user")
    public ResponseEntity<String> getUserInfo(@PathVariable String platform,
                                            @RequestParam String code,
                                            @RequestParam String state
                                            ){
        OAuth2UseCase oAuth2UseCase = oAuth2UseCaseResolver.resolve(platform);
        oAuth2UseCase.getUserInfo(state, code);
        return ResponseEntity.ok("소셜 로그인 성공");
    }

}