package com.ureca.picky_be.jpa.movie;

import com.ureca.picky_be.jpa.config.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.Date;

@Getter
@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MovieSoftDelete extends BaseEntity {
    @Id
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Date releaseDate;

    private String posterUrl;

    @Column(nullable = false)
    @ColumnDefault("0.0")
    private double totalRating;

    @Column(nullable = false)
    private String plot;

    @Column(nullable = false)
    private int runningTime;

    private String trailerUrl;

    private String ostUrl;
}
