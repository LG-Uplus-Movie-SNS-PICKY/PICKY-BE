package com.ureca.picky_be.base.presentation.controller.board;


import com.ureca.picky_be.base.business.board.BoardManagingUseCase;
import com.ureca.picky_be.base.business.board.dto.AddBoardCommentReq;
import com.ureca.picky_be.base.business.board.dto.AddBoardReq;
import com.ureca.picky_be.base.business.board.dto.GetAllBoardComments;
import com.ureca.picky_be.base.business.board.dto.UpdateBoardReq;
import com.ureca.picky_be.global.response.ApiResponse;
import com.ureca.picky_be.global.success.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/board")
public class BoardController {

    private final BoardManagingUseCase boardManagingUseCase;

    @PostMapping("")
    @Operation(summary = "게시글 생성(Create)", description = "사용자가 입력한 게시글 생성, contentType으로 'PHOTO', 'VIDEO'로 입력해야합니다. 또한, 현재 글만 입력 가능합니다!")
    public SuccessCode createBoard(@RequestBody AddBoardReq req) {
        boardManagingUseCase.addBoard(req);
        return SuccessCode.CREATE_BOARD_SUCCESS;
    }

    @PostMapping("/{boardId}")
    @Operation(summary = "게시글 수정(Update)", description = "사용자가 작성한 게시글 수정")
    public SuccessCode updateBoard(@PathVariable Long boardId, @RequestBody UpdateBoardReq req) {
        boardManagingUseCase.updateBoard(boardId, req);
        return SuccessCode.UPDATE_BOARD_SUCCESS;
    }

    @PostMapping("/{boardId}/comment")
    @Operation(summary = "게시글 댓글 생성(Create)", description = "사용자가 게시들에 댓글 작성")
    public SuccessCode addBoardComment(@PathVariable Long boardId, @RequestBody AddBoardCommentReq req) {
        boardManagingUseCase.addBoardComment(req, boardId);
        return SuccessCode.CREATE_BOARD_COMMENT_SUCCESS;
    }

//    @GetMapping("/{boardId}/comments")
//    @Operation(summary = "게시글 댓글 조회(Read)", description = "사용자가 게시들 댓글들 조회")
//    public ResponseEntity<ApiResponse<GetAllBoardComments>> getBoardComments(@PathVariable Long boardId) {
//
//
//    }


}
