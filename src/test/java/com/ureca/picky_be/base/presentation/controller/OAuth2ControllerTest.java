package com.ureca.picky_be.base.presentation.controller;

import com.ureca.picky_be.base.presentation.controller.auth.OAuth2Controller;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("local")
@SpringBootTest
public class OAuth2ControllerTest {

    @Autowired
    OAuth2Controller controller;

    @Test
    void getNaverLoginUrlTest() {
        String url = controller.getNaverLoginUrl().getBody();
        System.out.println(url);
    }
}
