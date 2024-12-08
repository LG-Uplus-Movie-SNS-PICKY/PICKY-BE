package com.ureca.picky_be.base.business.movie;

import com.ureca.picky_be.base.business.movie.dto.*;
import com.ureca.picky_be.base.business.user.dto.GetMoviesForRegisReq;
import com.ureca.picky_be.base.business.user.dto.GetMoviesForRegisResp;
import com.ureca.picky_be.global.success.SuccessCode;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;
import java.util.List;

public interface MovieUseCase {
    SuccessCode addMovie(AddMovieReq addMovieReq);
    GetMovieDetailResp getMovieDetail(Long movieId);
    List<GetMoviesForRegisResp> getMoviesByGenre(GetMoviesForRegisReq getMoviesForRegisReq);
    SuccessCode updateMovie(Long movieId, UpdateMovieReq updateMovieReq);
    List<GetSimpleMovieResp> getRecommends();
    List<GetSimpleMovieResp> getTop10();
    List<GetSimpleMovieResp> getMoviesByGenre(Long genreId, Long lastMovieId, Integer lastLikeCount);
    boolean movieLike(Long movieId);
    List<GetGenres> getGenres();
    Slice<GetSimpleMovieResp> getMoviesOrderByCreatedAt(Long lastMovieId, LocalDateTime createdAt, int size);
}
