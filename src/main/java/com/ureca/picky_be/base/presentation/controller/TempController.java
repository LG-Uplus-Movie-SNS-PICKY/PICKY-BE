package com.ureca.picky_be.base.presentation.controller;

import com.ureca.picky_be.base.persistence.user.UserRepository;
import com.ureca.picky_be.global.exception.CustomException;
import com.ureca.picky_be.global.exception.ErrorCode;
import com.ureca.picky_be.global.web.JwtTokenProvider;
import com.ureca.picky_be.global.web.LocalJwtDto;
import com.ureca.picky_be.jpa.entity.user.User;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TempController {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Value("${token.temp.id}")
    private String registeredId;

    @Value("${token.temp.password}")
    private String registeredPassword;


    @Operation(summary = "임시 토큰 발급 api 개발할 때만 쓰는 용도!!!!!!!", description = "현재 소셜로그인이 모두 서버에 연결된 상태이므로 여기 RequestParam에 유저 아이디 넣으면 토큰 반환")
    @GetMapping("/tokentokentoken")
    public LocalJwtDto getTokentokentoken(@RequestParam Long userId, @RequestParam String tempId, @RequestParam String tempPassword) {
        if(registeredId.equals(tempId) && registeredPassword.equals(tempPassword)) {
            User user = userRepository.findById(userId).get();
            return jwtTokenProvider.generate(user.getId(), user.getRole().toString());
        }
        throw new CustomException(ErrorCode.VALIDATION_ERROR);
    }
}
