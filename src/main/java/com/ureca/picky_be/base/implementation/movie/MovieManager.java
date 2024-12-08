package com.ureca.picky_be.base.implementation.movie;

import com.ureca.picky_be.base.business.movie.dto.*;
import com.ureca.picky_be.base.business.user.dto.GetMoviesForRegisReq;
import com.ureca.picky_be.base.business.user.dto.GetMoviesForRegisResp;
import com.ureca.picky_be.base.persistence.movie.*;
import com.ureca.picky_be.base.persistence.movieworker.MovieWorkerRepository;
import com.ureca.picky_be.base.persistence.user.UserRepository;
import com.ureca.picky_be.global.exception.CustomException;
import com.ureca.picky_be.global.exception.ErrorCode;
import com.ureca.picky_be.global.success.SuccessCode;
import com.ureca.picky_be.jpa.genre.Genre;
import com.ureca.picky_be.jpa.lineReview.SortType;
import com.ureca.picky_be.jpa.movie.*;
import com.ureca.picky_be.jpa.movieworker.MovieWorker;
import com.ureca.picky_be.jpa.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MovieManager {
    private final MovieRepository movieRepository;
    private final MovieGenreRepository movieGenreRepository;
    private final MovieBehindVideoRepository movieBehindVideoRepository;
    private final MovieWorkerRepository movieWorkerRepository;
    private final FilmCrewRepository filmCrewRepository;
    private final GenreRepository genreRepository;
    private final MovieLikeRepository movieLikeRepository;
    private final UserRepository userRepository;

    /* 회원가입시에 영화 리스트 전송 */
    @Transactional(readOnly = true)
    public List<GetMoviesForRegisResp> getMoviesByGenres(GetMoviesForRegisReq getMoviesForRegisReq) {
        //TODO: 장르 없을 때 예외
        validateGenreIds(getMoviesForRegisReq.genreIds());
        Pageable pageable = PageRequest.of(0, 45);
        return movieRepository.findMovieByGenresOrderByTotalRating(getMoviesForRegisReq.genreIds(), pageable);
    }

    private void validateGenreIds(List<Long> genreIds) {
        List<Long> invalidGenreIds = genreIds.stream()
                .filter(id -> !genreRepository.existsById(id))
                .toList();
        if (!invalidGenreIds.isEmpty()) {
            throw new CustomException(ErrorCode.GENRE_NOT_FOUND);
        }
    }

    /* 영화 추가 */
    public SuccessCode addMovie(AddMovieReq addMovieReq) {
        Movie movie = addMovieInfo(addMovieReq);
        List<MovieBehindVideo> movieBehindVideos = addMovieBehindVideos(addMovieReq.movieBehindVideos(), movie);
        List<MovieGenre> movieGenres = addMovieGenres(addMovieReq.movieInfo().genres(), movie);
        List<FilmCrew> actors = addActors(addMovieReq.movieInfo().credits(), movie);
        List<FilmCrew> directors = addDirectors(addMovieReq.movieInfo().credits(), movie);
        return SuccessCode.CREATE_MOVIE_SUCCESS;
    }

    private Movie addMovieInfo(AddMovieReq addMovieReq) {
        Movie movie = Movie.builder()
                .id(addMovieReq.movieInfo().id())
                .title(addMovieReq.movieInfo().title())
                .releaseDate(addMovieReq.movieInfo().releaseDate())
                .posterUrl(addMovieReq.movieInfo().posterUrl())
                .backdropUrl(addMovieReq.movieInfo().backdropUrl())
                .totalRating(2.5)
                .plot(addMovieReq.movieInfo().plot())
                .runningTime(addMovieReq.movieInfo().runtime())
                .trailerUrl(addMovieReq.trailer())
                .ostUrl(addMovieReq.ost())
                .build();
        return movieRepository.save(movie);
    }

    private List<MovieBehindVideo> addMovieBehindVideos(List<String> movieBehindVideosReq, Movie movie) {
        List<MovieBehindVideo> movieBehindVideos = movieBehindVideosReq.stream()
                .map(url -> MovieBehindVideo.builder()
                        .url(url)
                        .movie(movie)
                        .build())
                .toList();
        return movieBehindVideoRepository.saveAll(movieBehindVideos);
    }

    private List<MovieGenre> addMovieGenres(List<AddMovieReq.MovieInfo.GenreInfo> genres, Movie movie) {
        List<MovieGenre> movieGenres = genres.stream()
                .map(genre -> MovieGenre.builder()
                        .movie(movie)
                        .genreId(genre.id())
                        .build())
                .toList();
        return movieGenreRepository.saveAll(movieGenres);
    }

    private List<FilmCrew> addActors(AddMovieReq.MovieInfo.Credits credits, Movie movie) {
        List<FilmCrew> filmCrews = credits.cast().stream()
                .limit(10)
                .map(cast -> {
                    MovieWorker movieWorker = movieWorkerRepository.findById(cast.id())
                            .orElseGet(() -> movieWorkerRepository.save(
                                    MovieWorker.builder()
                                            .id(cast.id())
                                            .name(cast.name())
                                            .profileUrl(cast.profileUrl())
                                            .build()
                            ));

                    return FilmCrew.builder()
                            .movieWorker(movieWorker)
                            .movie(movie)
                            .role(cast.role())
                            .filmCrewPosition(FilmCrewPosition.ACTOR)
                            .build();
                })
                .toList();
        return filmCrewRepository.saveAll(filmCrews);
    }

    private List<FilmCrew> addDirectors(AddMovieReq.MovieInfo.Credits credits, Movie movie) {
        List<FilmCrew> filmCrews = credits.getDirectingCrew().stream()
                .limit(10)
                .map(cast -> {
                    MovieWorker movieWorker = movieWorkerRepository.findById(cast.id())
                            .orElseGet(() -> movieWorkerRepository.save(
                                    MovieWorker.builder()
                                            .id(cast.id())
                                            .name(cast.name())
                                            .profileUrl(cast.profileUrl())
                                            .build()
                            ));

                    return FilmCrew.builder()
                            .movieWorker(movieWorker)
                            .movie(movie)
                            .role("감독감독~!")
                            .filmCrewPosition(FilmCrewPosition.DIRECTOR)
                            .build();
                })
                .toList();
        return filmCrewRepository.saveAll(filmCrews);
    }

    /* 영화 상세 정보 get */
    public Movie getMovie(Long movieId) {
        return movieRepository.findById(movieId)
                .orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));
    }

    public List<MovieBehindVideo> getMovieBehindVideos(Long movieId) {
        return movieBehindVideoRepository.findAllByMovieId(movieId);
    }

    public List<Genre> getGenre(Long movieId) {
        List<Long> genreIds = movieGenreRepository.getGenreIdsByMovieId(movieId);
        List<Genre> genres = genreRepository.findAllByIdIn(genreIds);
        if (genres.isEmpty()) {
            throw new CustomException(ErrorCode.GENRE_NOT_FOUND);
        }
        return genres;
    }

    public List<FilmCrew> getActors(Movie movie) {
        List<FilmCrew> actors = filmCrewRepository.findByMovieAndFilmCrewPosition(movie, FilmCrewPosition.ACTOR);
        if (actors.isEmpty()) {
            throw new CustomException(ErrorCode.ACTOR_NOT_FOUND);
        }
        return actors;
    }

    public List<FilmCrew> getDirectors(Movie movie) {
        List<FilmCrew> directors = filmCrewRepository.findByMovieAndFilmCrewPosition(movie, FilmCrewPosition.DIRECTOR);
        if(directors.isEmpty()) {
            throw new CustomException(ErrorCode.DIRECTOR_NOT_FOUND);
        }
        return directors;
    }

    public boolean getMovieLike(Long movieId, Long userId){
        return movieLikeRepository.existsByMovieIdAndUserId(movieId, userId);
    }

    /* 영화 수정 */
    public SuccessCode updateMovie (Long movieId, UpdateMovieReq updateMovieReq){
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));
        if(updateMovieReq.movieInfo() != null){
            updateMovieInfo(movie, updateMovieReq);
            if(updateMovieReq.movieInfo().genres() != null){
                updateMovieGenre(movie, updateMovieReq.movieInfo().genres());
            }
            if(updateMovieReq.movieInfo().credits() != null){
                if(updateMovieReq.movieInfo().credits().cast() != null){
                    updateActors(movie, updateMovieReq.movieInfo().credits().cast());
                }

                if(updateMovieReq.movieInfo().credits().crew() != null){
                    updateDirectors(movie, updateMovieReq.movieInfo().credits().getDirectingCrew());
                }
            }
        }
        return SuccessCode.UPDATE_MOVIE_SUCCESS;
    }

    private Movie updateMovieInfo(Movie movie, UpdateMovieReq updateMovieReq){
        movie = movie.updateMovie(updateMovieReq);
        return movieRepository.save(movie);
    }

    private List<MovieGenre> updateMovieGenre(Movie movie, List<UpdateMovieReq.MovieInfo.GenreInfo> genres){
        movieGenreRepository.deleteMovieGenreByMovieId(movie.getId());
        List<MovieGenre> movieGenres = genres.stream()
                .map(genre -> MovieGenre.builder()
                        .movie(movie)
                        .genreId(genre.id())
                        .build())
                .toList();
        return movieGenreRepository.saveAll(movieGenres);
    }

    private List<FilmCrew> updateActors(Movie movie, List<UpdateMovieReq.MovieInfo.Credits.Cast> casts){
        filmCrewRepository.deleteActorsByMovieId(movie.getId());
        List<FilmCrew> filmCrews = casts.stream()
                .limit(10)
                .map(cast -> {
                    MovieWorker movieWorker = movieWorkerRepository.findById(cast.id())
                            .orElseGet(() -> movieWorkerRepository.save(
                                    MovieWorker.builder()
                                            .id(cast.id())
                                            .name(cast.name())
                                            .profileUrl(cast.profileUrl())
                                            .build()
                            ));

                    return FilmCrew.builder()
                            .movieWorker(movieWorker)
                            .movie(movie)
                            .role(cast.role())
                            .filmCrewPosition(FilmCrewPosition.ACTOR)
                            .build();
                })
                .toList();
        return filmCrewRepository.saveAll(filmCrews);
    }

    private List<FilmCrew> updateDirectors(Movie movie, List<UpdateMovieReq.MovieInfo.Credits.Crew> crews){
        filmCrewRepository.deleteDirectorsByMovieId(movie.getId());
        List<FilmCrew> filmCrews = crews.stream()
                .limit(10)
                .map(crew -> {
                    MovieWorker movieWorker = movieWorkerRepository.findById(crew.id())
                            .orElseGet(() -> movieWorkerRepository.save(
                                    MovieWorker.builder()
                                            .id(crew.id())
                                            .name(crew.name())
                                            .profileUrl(crew.profileUrl())
                                            .build()
                            ));

                    return FilmCrew.builder()
                            .movieWorker(movieWorker)
                            .movie(movie)
                            .role("감독감독~!")
                            .filmCrewPosition(FilmCrewPosition.DIRECTOR)
                            .build();
                })
                .toList();
        return filmCrewRepository.saveAll(filmCrews);
    }

    /* 기준에 따른 영화 리스트 get */
    public List<GetSimpleMovieResp> getRecommends(){
        Pageable pageable = PageRequest.of(0, 30);
        return movieRepository.findTop30MoviesWithLikes(pageable);
    }

    public List<GetSimpleMovieResp> getTop10(){
        Pageable pageable = PageRequest.of(0, 10);
        return movieRepository.findTop10MoviesWithLikes(pageable);
    }

    public List<GetSimpleMovieResp> getMoviesByGenre(Long genreId, Long lastMovieId, Integer lastLikeCount){
/*        // 영화 존재 여부 확인
        if (lastMovieId != null && !movieRepository.existsById(lastMovieId)) {
            throw new CustomException(ErrorCode.MOVIE_NOT_FOUND);
        }*/

        //validateCursor(genreId, lastMovieId);
        System.out.println(23947923);
        System.out.println(movieRepository.findMoviesByGenreIdWithLikesUsingCursor(genreId, lastLikeCount, lastMovieId, PageRequest.ofSize(12)));
        System.out.println("sefefwefw");
        return movieRepository.findMoviesByGenreIdWithLikesUsingCursor(genreId, lastLikeCount, lastMovieId, PageRequest.ofSize(12));
    }

    private void validateCursor(Long genreId, Long movieId) {
        // 첫 요청일 경우
        if (movieId == null) return;

        // genreId 유효성 검사
        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new CustomException(ErrorCode.GENRE_NOT_FOUND));
    }

    @Transactional
    public boolean movieLike(Long movieId, Long userId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return movieLikeRepository.findByMovieIdAndUserId(movieId, userId)
                .map(movieLike -> {
                    movieLikeRepository.delete(movieLike);
                    return false;
                })
                .orElseGet(() -> {
                    createNewMovieLike(movie, user);
                    return true;
                });
    }

    private void createNewMovieLike(Movie movie, User user){
        try{
            MovieLike newMovieLike = MovieLike.builder()
                    .movie(movie)
                    .user(user)
                    .build();
            movieLikeRepository.save(newMovieLike);
        } catch (Exception e){
            throw new CustomException(ErrorCode.MOVIE_LIKE_FAILED);
        }
    }

    public List<Genre> getGenres(){
        return genreRepository.findAll();
    }

    public List<GetSimpleMovieProjection> getMoviesOrderByCreatedAt(Long lastMovieId, LocalDateTime createdAt, int size){
        return movieRepository.findMoviesOrderByCreatedAtUsingCursor(lastMovieId, createdAt, PageRequest.ofSize(size));
    }
}
