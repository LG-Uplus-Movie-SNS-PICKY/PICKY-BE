package com.ureca.picky_be.jpa.linereview;

import com.ureca.picky_be.jpa.config.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LineReviewSoftDelete extends BaseEntity {
    @Id
    private Long id;

    @Column(name="user_id", nullable = false)
    private Long userId;

    @Column(name="movie_id", nullable = false)
    private Long movieId;

    @ColumnDefault("0.0")
    private double rating;

    @Column(nullable = false)
    private String context;

    private boolean isDeleted;
}
