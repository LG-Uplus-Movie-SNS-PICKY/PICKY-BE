package com.ureca.picky_be.jpa.user;

import com.ureca.picky_be.jpa.config.BaseEntity;
import com.ureca.picky_be.jpa.genre.Genre;
import com.ureca.picky_be.jpa.linereview.Preference;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class UserGenrePreference extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="genre_id")
    private Genre genre;

    @Column(nullable = false)
    private int preferenceValue;



}
