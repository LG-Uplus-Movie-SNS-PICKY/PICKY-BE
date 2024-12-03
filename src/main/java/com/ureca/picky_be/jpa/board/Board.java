package com.ureca.picky_be.jpa.board;

import com.ureca.picky_be.base.business.board.dto.AddBoardContentReq;
import com.ureca.picky_be.jpa.config.BaseEntity;
import com.ureca.picky_be.jpa.movie.Movie;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Getter
@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id", nullable=false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="movie_id", nullable=false)
    private Movie movie;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardContent> contents = new ArrayList<>();

    private String context;

    private boolean isSpoiler;


    public void updateBoard(String context, boolean isSpoiler) {
        this.context = context;
        this.isSpoiler = isSpoiler;
    }

    public static Board of(Long userId, Movie movie, String context, boolean isSpoiler, List<AddBoardContentReq> addBoardContentReqs) {

        Board board = Board.builder()
                .userId(userId)
                .movie(movie)
                .context(context)
                .isSpoiler(isSpoiler)
                .contents(new ArrayList<>())
                .build();

        for (AddBoardContentReq dto : addBoardContentReqs) {
            BoardContent boardContent = BoardContent.of(board, dto.contentUrl(), dto.type());
            board.addBoardContent(boardContent);
        }
        return board;
    }

    public void addBoardContent(BoardContent boardContent) {
        this.contents.add(boardContent);
    }



}
