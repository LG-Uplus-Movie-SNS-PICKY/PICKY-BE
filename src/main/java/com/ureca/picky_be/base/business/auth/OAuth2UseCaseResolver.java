package com.ureca.picky_be.base.business.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2UseCaseResolver {

    private final Map<String, OAuth2UseCase> useCaseMap;

    public OAuth2UseCase resolve(String platform) {
        String key = platform.toLowerCase() + "Service";
        if (!useCaseMap.containsKey(key)) {
            throw new IllegalArgumentException("Unsupported platform: " + platform);
        }
        return useCaseMap.get(key);
    }
}
