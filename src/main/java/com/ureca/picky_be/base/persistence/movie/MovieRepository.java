package com.ureca.picky_be.base.persistence.movie;

import com.ureca.picky_be.base.business.movie.dto.GetSimpleMovieProjection;
import com.ureca.picky_be.base.business.movie.dto.GetSimpleMovieResp;
import com.ureca.picky_be.base.business.user.dto.GetMoviesForRegisResp;
import com.ureca.picky_be.jpa.movie.Movie;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    @Query("""
    SELECT new com.ureca.picky_be.base.business.user.dto.GetMoviesForRegisResp(
        m.id,
        m.title,
        m.posterUrl
    )
    FROM Movie m
    JOIN MovieGenre mg ON mg.movieId = m
    WHERE mg.genreId IN :genreIds
    GROUP BY m.id, m.posterUrl, m.totalRating, m.createdAt
    ORDER BY m.totalRating DESC, m.createdAt DESC
""")
    List<GetMoviesForRegisResp> findMovieByGenresOrderByTotalRating(@Param("genreIds") List<Long> genreIds, Pageable pageable);

    @Query("""
    SELECT new com.ureca.picky_be.base.business.movie.dto.GetSimpleMovieResp(
        m.id,
        m.title,
        CAST(COUNT(ml) AS int),
        m.totalRating,
        m.posterUrl,
        m.backdropUrl
    )
    FROM Movie m
    LEFT JOIN MovieLike ml ON ml.movie.id = m.id
    GROUP BY m.id, m.title, m.totalRating, m.posterUrl, m.backdropUrl
    ORDER BY m.totalRating DESC
""")
    List<GetSimpleMovieResp> findTop30MoviesWithLikes(Pageable pageable);

    @Query("""
    SELECT new com.ureca.picky_be.base.business.movie.dto.GetSimpleMovieResp(
        m.id,
        m.title,
        CAST(COUNT(ml) AS int),
        m.totalRating,
        m.posterUrl,
        m.backdropUrl
    )
    FROM Movie m
    LEFT JOIN MovieLike ml ON ml.movie.id = m.id
    GROUP BY m.id, m.title, m.totalRating, m.posterUrl, m.backdropUrl
    ORDER BY m.totalRating DESC
""")
    List<GetSimpleMovieResp> findTop10MoviesWithLikes(Pageable pageable);

    @Query("""
    SELECT new com.ureca.picky_be.base.business.movie.dto.GetSimpleMovieResp(
        m.id,
        m.title,
        CAST(COUNT(ml) AS int),
        m.totalRating,
        m.posterUrl,
        m.backdropUrl
    )
    FROM Movie m
    JOIN MovieGenre mg ON mg.movieId = m
    LEFT JOIN MovieLike ml ON ml.movie.id = m.id
    WHERE mg.genreId = :genreId
    GROUP BY m.id, m.title, m.totalRating, m.posterUrl, m.backdropUrl
    HAVING (
        :lastMovieId IS NULL AND :lastLikeCount IS NULL
        OR (CAST(COUNT(ml) AS int) < :lastLikeCount)
        OR (CAST(COUNT(ml) AS int) = :lastLikeCount)
    )
    ORDER BY COUNT(ml) DESC, m.totalRating DESC
""")
    List<GetSimpleMovieResp> findMoviesByGenreIdWithLikesUsingCursor(
            @Param("genreId") Long genreId,
            @Param("lastLikeCount") Integer lastLikeCount,
            @Param("lastMovieId") Long lastMovieId,
            Pageable pageable
    );

    @Query(value = """
    SELECT
        mpl.playlist.id AS playlistId,
        m.id AS movieId,
        m.title AS title,
        CAST(COUNT(ml) AS int) AS likes,
        m.totalRating AS totalRating,
        m.posterUrl AS posterUrl,
        m.backdropUrl AS backdropUrl
    FROM Movie m
    JOIN MoviePlayList mpl ON mpl.movieId.id = m.id
    LEFT JOIN MovieLike ml ON ml.movie.id = m.id
    WHERE mpl.playlist.id IN :playlistIds
    GROUP BY mpl.playlist.id, m.id, m.title, m.totalRating, m.posterUrl, m.backdropUrl
    ORDER BY m.id DESC
    """)
    List<GetSimpleMovieProjection> getMoviesByPlaylistIds(@Param("playlistIds") List<Long> playlistIds);

    @Query("""
    SELECT
        m.id AS movieId,
        m.title AS title,
        CAST(COUNT(ml) AS int) AS likes,
        m.totalRating AS totalRating,
        m.posterUrl AS posterUrl,
        m.backdropUrl AS backdropUrl
    FROM Movie m
    LEFT JOIN MovieLike ml ON ml.movie.id = m.id
    WHERE (:lastMovieId IS NULL OR m.id < :lastMovieId)
    GROUP BY m.id, m.title, m.totalRating, m.posterUrl, m.backdropUrl
    ORDER BY m.id DESC, :createdAt DESC
    """)
    List<GetSimpleMovieProjection> findMoviesOrderByCreatedAtUsingCursor(Long lastMovieId, LocalDateTime createdAt, Pageable pageable);
}

