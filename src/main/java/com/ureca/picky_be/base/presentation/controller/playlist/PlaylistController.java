package com.ureca.picky_be.base.presentation.controller.playlist;

import com.ureca.picky_be.base.business.playlist.PlaylistUseCase;
import com.ureca.picky_be.base.business.playlist.dto.GetPlaylistResp;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/playlist")
public class PlaylistController {

    private final PlaylistUseCase playlistUseCase;

    @Operation(summary = "전체 플레이리스트 조회", description = "플레이리스트 3개씩 페이징 조회. size는 필요한 개수만큼(ex: 메인페이지라면 3개). last-playlist-id는 리스트의 가장 마지막에 있는 플레이리스트의 id")
    @GetMapping("/all")
    public Slice<GetPlaylistResp> getPlaylist(
            @RequestParam(value = "last-playlist-id", required = false) Long lastPlaylistId,
            @RequestParam(value = "size", required = false, defaultValue = "3") int size){
        return playlistUseCase.getPlaylist(lastPlaylistId, size);
    }
}
