package com.ureca.picky_be.base.presentation.controller.admin;


import com.ureca.picky_be.base.business.movie.MovieUseCase;
import com.ureca.picky_be.base.business.movie.dto.GetSimpleMovieResp;
import com.ureca.picky_be.base.business.playlist.PlaylistUseCase;
import com.ureca.picky_be.base.business.playlist.dto.AddPlaylistReq;
import com.ureca.picky_be.base.business.playlist.dto.AddPlaylistResp;
import com.ureca.picky_be.base.business.playlist.dto.UpdatePlaylistReq;
import com.ureca.picky_be.base.business.playlist.dto.UpdatePlaylistResp;
import com.ureca.picky_be.global.success.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/playlist")
public class AdminPlaylistController {

    private final MovieUseCase movieUseCase;
    private final PlaylistUseCase playlistUseCase;

    @Operation(summary = "플레이리스트 추가를 위해 영화리스트 조회 - 무한스크롤", description = "(담당자:김)last-movie-id: 첫 요청이라면 null 아니라면 이전 리스트의 가장 마지막 영화id, created-at: 마지막 영화의 생성일, size: 한페이지에 원하는 영화 개수, 현재 영화 아이디 내림차순으로 조회.")
    @GetMapping("movies")
    public Slice<GetSimpleMovieResp> getMovieListForCreatingPlaylist(
            @RequestParam(value = "last-movie-id", required = false) Long lastMovieId,
            @RequestParam(value = "created-at", required = false) LocalDateTime createdAt,
            @RequestParam(value = "size", required = false, defaultValue = "12") int size
    ){
        return movieUseCase.getMoviesOrderByCreatedAt(lastMovieId, createdAt, size);
    }

    @Operation(summary = "플레이리스트 추가", description = "(담당자:김)플레이리스트 추가 api")
    @PostMapping
    public AddPlaylistResp createPlaylist(@RequestBody AddPlaylistReq addPlaylistReq) {
        return playlistUseCase.addPlaylist(addPlaylistReq);
    }

    @Operation(summary = "플레이리스트 업데이트", description = "(담당자:김)플레이리스트 수정 -> 수정할 플리id, 들어갈 영화ids null불가능. 제목 null가능 (null일 경우 수정 안 함)")
    @PatchMapping
    public UpdatePlaylistResp updatePlaylist(@RequestBody UpdatePlaylistReq updatePlaylistReq) {
        return playlistUseCase.updatePlaylist(updatePlaylistReq);
    }

    @DeleteMapping
    public SuccessCode deletePlaylist(@RequestParam Long playlistId) {
        return playlistUseCase.deletePlaylist(playlistId);
    }
}