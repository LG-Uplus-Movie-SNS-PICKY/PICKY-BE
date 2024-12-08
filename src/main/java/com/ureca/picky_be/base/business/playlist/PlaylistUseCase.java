package com.ureca.picky_be.base.business.playlist;

import com.ureca.picky_be.base.business.playlist.dto.*;
import com.ureca.picky_be.global.success.SuccessCode;
import org.springframework.data.domain.Slice;

public interface PlaylistUseCase {
    Slice<GetPlaylistResp> getPlaylist(Long lastPlaylistId, Integer size);
    AddPlaylistResp addPlaylist(AddPlaylistReq addPlaylistReq);
    UpdatePlaylistResp updatePlaylist(UpdatePlaylistReq updatePlaylistReq);
    SuccessCode deletePlaylist(Long playlistId);
}
