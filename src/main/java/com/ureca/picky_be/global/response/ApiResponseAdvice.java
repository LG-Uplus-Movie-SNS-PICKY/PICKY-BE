package com.ureca.picky_be.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ureca.picky_be.global.exception.GlobalExceptionHandler;
import com.ureca.picky_be.global.response.ApiResponse;
import com.ureca.picky_be.global.success.SuccessCode;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
@RestControllerAdvice(
        basePackages = "com.ureca.picky_be.base",
        basePackageClasses = GlobalExceptionHandler.class
)
public class ApiResponseAdvice implements ResponseBodyAdvice<Object> {

    private final ObjectMapper objectMapper;

    public ApiResponseAdvice(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper.copy();
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // JSON 컨버터에 대해서만 적용
        return MappingJackson2HttpMessageConverter.class.isAssignableFrom(converterType);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        try {

            if (body instanceof SuccessCode) {
                return ApiResponse.success((SuccessCode) body);
            }

            // 이미 ApiResponse 타입이면 그대로 반환
            if (body instanceof ApiResponse) {
                return body;
            }

            // String 응답은 JSON 문자열로 변환
            if (body instanceof String) {
                return objectMapper.writeValueAsString(
                        ApiResponse.success(SuccessCode.GENERAL_SUCCESS, body)
                );
            }

            // ResponseEntity 응답 처리
            if (body instanceof ResponseEntity) {
                ResponseEntity<?> entity = (ResponseEntity<?>) body;
                return ResponseEntity
                        .status(entity.getStatusCode())
                        .body(ApiResponse.success(SuccessCode.GENERAL_SUCCESS, entity.getBody()));
            }

            // null 응답 처리
            if (body == null) {
                return ApiResponse.success(SuccessCode.GENERAL_SUCCESS);
            }

            // 일반 객체를 ApiResponse로 감싸기
            return ApiResponse.success(SuccessCode.GENERAL_SUCCESS, body);

        } catch (Exception e) {
            throw new RuntimeException("응답 처리 중 오류가 발생했습니다.", e);
        }
    }
}
