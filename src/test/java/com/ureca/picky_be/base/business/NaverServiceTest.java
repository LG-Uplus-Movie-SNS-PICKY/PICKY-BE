package com.ureca.picky_be.base.business;

import com.ureca.picky_be.base.business.auth.NaverService;
import com.ureca.picky_be.config.oAuth2.NaverConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("local")
@SpringBootTest
public class NaverServiceTest {

    @Autowired
    private NaverService naverService;

    @Test
    void getLoginUrlTest(){
        System.out.println(naverService.getLoginUrl());
    }
}
