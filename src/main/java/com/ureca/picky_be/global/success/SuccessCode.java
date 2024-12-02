package com.ureca.picky_be.global.success;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SuccessCode {
    // User
    REQUEST_FRONT_SUCCESS(200, "프론트에 요청 완료"),
    REQUEST_DELETE_ACCOUNT_SUCCESS(200, "유저 계정 삭제 완료"),
    UPDATE_USER_SUCCESS(200, "유저 정보 업데이트 완료"),

    //Movie
    CREATE_MOVIE_SUCCESS(201, "영화 생성 완료"),
    DELETE_MOVIE_SUCCESS(200, "영화 삭제 완료"),
    GENERAL_SUCCESS(200, "요청이 성공적으로 처리되었습니다."),

    // Board
    CREATE_BOARD_SUCCESS(201, "게시글 생성 완료"),
    UPDATE_BOARD_SUCCESS(200, "게시글 수정 완료"),
    CREATE_BOARD_COMMENT_SUCCESS(201, "게시글 댓글 생성 완료");

    private final int status;    // HTTP 상태 코드
    private final String message; // 성공 메시지
}


///return CREATE_MOVIE_SUCCESS.getMessage();