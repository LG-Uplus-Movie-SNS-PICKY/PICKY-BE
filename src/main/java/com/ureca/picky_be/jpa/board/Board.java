package com.ureca.picky_be.jpa.board;

import com.ureca.picky_be.jpa.config.BaseEntity;
import com.ureca.picky_be.jpa.movie.Movie;
import com.ureca.picky_be.jpa.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

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

//    @Column(name = "movie_id", nullable=false)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="movie_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Movie movieId;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "board", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<BoardContent> contents = new ArrayList<>();

    private String context;

    private boolean isSpoiler;


}
