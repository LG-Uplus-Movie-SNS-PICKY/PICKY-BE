package com.ureca.picky_be.base.business.board.dto.boardDto;

public record AddBoardReq(String boardContext, Long movieId, boolean isSpoiler) {
}
