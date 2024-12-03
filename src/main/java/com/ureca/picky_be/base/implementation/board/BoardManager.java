package com.ureca.picky_be.base.implementation.board;

import com.ureca.picky_be.base.business.board.dto.AddBoardContentReq;
import com.ureca.picky_be.base.business.board.dto.AddBoardReq;
import com.ureca.picky_be.base.business.board.dto.UpdateBoardReq;
import com.ureca.picky_be.base.persistence.board.BoardRepository;
import com.ureca.picky_be.base.persistence.board.BoardCommentRepository;
import com.ureca.picky_be.base.persistence.board.BoardContentRepository;
import com.ureca.picky_be.base.persistence.board.BoardLikeRepository;
import com.ureca.picky_be.base.persistence.movie.MovieRepository;
import com.ureca.picky_be.global.exception.CustomException;
import com.ureca.picky_be.global.exception.ErrorCode;
import com.ureca.picky_be.jpa.board.Board;
import com.ureca.picky_be.jpa.board.BoardComment;
import com.ureca.picky_be.jpa.board.BoardContent;
import com.ureca.picky_be.jpa.movie.Movie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BoardManager {
    private final BoardRepository boardRepository;
    private final MovieRepository movieRepository;
    private final BoardCommentRepository boardCommentRepository;
    private final BoardContentRepository boardContentRepository;

    @Transactional
    public void addBoard(Long userId, AddBoardReq req) {
        Movie movie = movieRepository.findById(req.movieId())
                .orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));

        if(req.contents().size() > 5) {
            throw new CustomException(ErrorCode.BOARD_CONTENT_OVER_FIVE);
        }
        // TODO: S3 연동
        Board board = Board.of(userId, movie, req.boardContext(), req.isSpoiler(), req.contents());
        boardRepository.save(board);
    }

    @Transactional
    public void updateBoard(Long boardId, Long userId, UpdateBoardReq req) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));
        // 생성자를 통한 Board Update
        board.updateBoard(req.boardContext(), req.isSpoiler());
        boardRepository.save(board);

    }

//    public List<Board> getRecentMovieRelatedBoards(Long movieId, Long currentBoardId) {
//        // 최신순 기준으로 Board들을 가져온다.
//
//        List<Board> boards = boardRepository.getRecentMovieRelatedBoards(movieId, currentBoardId);
////        List<Integer> boardsIds = boardRepository.getRecentMovieRelatedBoardsIds(movieId, currentBoardId);
//
//        return
//
//    }

    public void checkBoardWriteUser(Long boardId, Long userId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        if(!board.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.BOARD_USER_NOT_WRITER);
        }
    }

    public void addBoardComment(String context, Long boardId, Long userId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));
        BoardComment comment = BoardComment.builder()
                .context(context)
                .userId(userId)
                .board(board)
                .build();

        boardCommentRepository.save(comment);
    }
}
