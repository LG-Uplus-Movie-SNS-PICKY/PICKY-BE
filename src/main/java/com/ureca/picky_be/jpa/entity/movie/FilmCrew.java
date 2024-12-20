package com.ureca.picky_be.jpa.entity.movie;

import com.ureca.picky_be.jpa.entity.config.BaseEntity;
import com.ureca.picky_be.jpa.entity.movieworker.MovieWorker;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FilmCrew extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="movie_worker_id")
    private MovieWorker movieWorker;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FilmCrewPosition filmCrewPosition;

    //    @Column(nullable = false)
    private String role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="movie_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Movie movie;

}
