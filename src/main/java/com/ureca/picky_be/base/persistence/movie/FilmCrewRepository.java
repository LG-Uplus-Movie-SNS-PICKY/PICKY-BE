package com.ureca.picky_be.base.persistence.movie;

import com.ureca.picky_be.jpa.movie.FilmCrew;
import com.ureca.picky_be.jpa.movie.FilmCrewPosition;
import com.ureca.picky_be.jpa.movie.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface FilmCrewRepository extends JpaRepository<FilmCrew, Integer> {
    List<FilmCrew> findByMovieAndFilmCrewPosition(Movie movie, FilmCrewPosition filmCrewPosition);

    @Modifying
    @Transactional
    @Query("DELETE FROM FilmCrew fc WHERE fc.movie.id = :movieId AND fc.filmCrewPosition= 'ACTOR' ")
    void deleteActorsByMovieId(@Param("movieId") Long movieId);

    @Modifying
    @Transactional
    @Query("DELETE FROM FilmCrew fc WHERE fc.movie.id = :movieId AND fc.filmCrewPosition= 'DIRECTOR' ")
    void deleteDirectorsByMovieId(@Param("movieId") Long movieId);
}
