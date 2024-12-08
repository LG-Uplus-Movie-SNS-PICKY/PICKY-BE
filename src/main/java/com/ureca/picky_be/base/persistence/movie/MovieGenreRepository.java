package com.ureca.picky_be.base.persistence.movie;

import com.ureca.picky_be.jpa.movie.MovieGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MovieGenreRepository extends JpaRepository<MovieGenre, Long> {
    @Query("SELECT mg.genreId FROM MovieGenre mg WHERE mg.movie.id = :movieId")
    List<Long> getGenreIdsByMovieId(@Param("movieId") Long movieId);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("DELETE FROM MovieGenre mg WHERE mg.movie.id = :movieId")
    void deleteMovieGenreByMovieId(@Param("movieId") Long movieId);
}
