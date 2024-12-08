package com.ureca.picky_be.base.implementation.mapper;

import com.ureca.picky_be.base.business.movie.dto.GetGenres;
import com.ureca.picky_be.base.business.movie.dto.GetMovieDetailResp;
import com.ureca.picky_be.base.business.movie.dto.GetSimpleMovieProjection;
import com.ureca.picky_be.base.business.movie.dto.GetSimpleMovieResp;
import com.ureca.picky_be.jpa.genre.Genre;
import com.ureca.picky_be.jpa.movie.FilmCrew;
import com.ureca.picky_be.jpa.movie.Movie;
import com.ureca.picky_be.jpa.movie.MovieBehindVideo;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class MovieDtoMapper {

    public GetMovieDetailResp toGetMovieDetailResp(
            Movie movie,
            List<MovieBehindVideo> movieBehindVideos,
            List<Genre> genres,
            List<FilmCrew> actors,
            List<FilmCrew> directors,
            boolean like
    ) {
        List<GetMovieDetailResp.MovieInfo.GenreInfo> genreInfoList = genres.stream()
                .map(genre -> new GetMovieDetailResp.MovieInfo.GenreInfo(genre.getId()))
                .toList();

        List<GetMovieDetailResp.MovieInfo.Credits.Cast> castList = actors.stream()
                .map(actor -> new GetMovieDetailResp.MovieInfo.Credits.Cast(
                        actor.getMovieWorker().getId(),
                        actor.getRole(),
                        actor.getMovieWorker().getName(),
                        actor.getMovieWorker().getProfileUrl()
                ))
                .toList();

        List<GetMovieDetailResp.MovieInfo.Credits.Crew> crewList = directors.stream()
                .map(director -> new GetMovieDetailResp.MovieInfo.Credits.Crew(
                        director.getMovieWorker().getId(),
                        "Director",
                        director.getMovieWorker().getName(),
                        director.getMovieWorker().getProfileUrl()
                ))
                .toList();

        List<String> movieBehindVideoUrls = movieBehindVideos.stream()
                .map(MovieBehindVideo::getUrl)
                .toList();

        return new GetMovieDetailResp(
                new GetMovieDetailResp.MovieInfo(
                        movie.getId(),
                        movie.getTitle(),
                        movie.getReleaseDate(),
                        movie.getPosterUrl(),
                        movie.getBackdropUrl(),
                        movie.getPlot(),
                        movie.getRunningTime(),
                        genreInfoList,
                        new GetMovieDetailResp.MovieInfo.Credits(castList, crewList)
                ),
                Optional.ofNullable(movie.getTrailerUrl()).orElse("Trailer not found"),
                Optional.ofNullable(movie.getOstUrl()).orElse("OST not found"),
                movieBehindVideoUrls,
                like
        );
    }

    public List<GetGenres> toGetGenres(List<Genre> genres) {
        return genres.stream()
                .map(genre -> new GetGenres(genre.getId(), genre.getName()))
                .toList();
    }

    public Slice<GetSimpleMovieResp> toGetSimpleMovies(List<GetSimpleMovieProjection> projections) {
        List<GetSimpleMovieResp> simpleMovieRespList = projections.stream()
                .map(projection -> new GetSimpleMovieResp(
                        projection.getMovieId(),
                        projection.getTitle(),
                        projection.getLikes(),
                        projection.getTotalRating(),
                        projection.getPosterUrl(),
                        projection.getBackdropUrl()
                ))
                .collect(Collectors.toList());

        return new SliceImpl<>(simpleMovieRespList);
    }
}