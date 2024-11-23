package com.ureca.picky_be.jpa.user;


import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Platform {
    NAVER("naver"),
    GOOGLE("google"),
    KAKAO("kakao");

    private final String value;

    Platform(String value) {
        this.value = value;
    }

    public static Platform from(String platform) {
        return Arrays.stream(Platform.values())
                .filter(it -> it.value.equalsIgnoreCase(platform))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 유저의 가입 플랫폼을 찾을 수 없습니다."));
    }
}
