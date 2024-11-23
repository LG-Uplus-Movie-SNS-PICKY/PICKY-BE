package com.ureca.picky_be.base.presentation.web;

import com.ureca.picky_be.jpa.user.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("local")
@SpringBootTest
public class JwtTokenProviderTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void 로컬토큰_발급(){
        LocalJwtDto localJwtDto = jwtTokenProvider.generate(1L, Role.USER.toString());
        System.out.println(localJwtDto);
    }
}
