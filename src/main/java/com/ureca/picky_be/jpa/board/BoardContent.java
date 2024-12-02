package com.ureca.picky_be.jpa.board;

import com.ureca.picky_be.jpa.config.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardContent extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @Column(nullable = false)
    private String contentUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BoardContentType boardContentType;

    public static BoardContent of(Board board, String contentUrl, String type){
        return BoardContent.builder()
                .board(board)
                .contentUrl(contentUrl)
                .boardContentType(BoardContentType.fromString(type))
                .build();
    }
}
