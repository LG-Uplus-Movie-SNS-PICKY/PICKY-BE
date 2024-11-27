package com.ureca.picky_be.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;


@Getter
public enum ErrorCode {
    // 기본 에러
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "E001", "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "E002", "인증에 실패했습니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "E003", "접근 권한이 없습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "E004", "리소스를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E005", "서버에 문제가 발생했습니다."),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "E006", "입력 값이 유효하지 않습니다."),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "E007", "유효성 검사가 실패했습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "E008", "지원하지 않는 요청 메서드입니다."),

    // USER
    USER_SAVE_FAILED(HttpStatus.BAD_REQUEST, "USR001", "유저 저장에 실패했습니다."),
    USER_DELETE_FAILED(HttpStatus.BAD_REQUEST, "USR002", "유저 삭제에 실패했습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USR003", "해당 유저가 존재하지 않습니다.");


    private final HttpStatus httpStatus; // HTTP 상태 코드
    private final String code;          // 에러 코드
    private final String message;       // 에러 메시지

    ErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
