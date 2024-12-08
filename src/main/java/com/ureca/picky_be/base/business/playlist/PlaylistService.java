package com.ureca.picky_be.base.business.playlist;

import com.ureca.picky_be.base.business.movie.dto.GetSimpleMovieProjection;
import com.ureca.picky_be.base.business.movie.dto.GetSimpleMovieResp;
import com.ureca.picky_be.base.business.playlist.dto.*;
import com.ureca.picky_be.base.implementation.mapper.PlaylistDtoMapper;
import com.ureca.picky_be.base.implementation.playlist.PlaylistManager;
import com.ureca.picky_be.global.exception.CustomException;
import com.ureca.picky_be.global.exception.ErrorCode;
import com.ureca.picky_be.global.success.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PlaylistService implements PlaylistUseCase{

    private final PlaylistManager playlistManager;
    private final PlaylistDtoMapper playlistDtoMapper;

    @Transactional(readOnly = true)
    public Slice<GetPlaylistResp> getPlaylist(Long lastPlaylistId, Integer size) {
        Slice<GetPlaylistProjection> playlistProjections = playlistManager.getPlaylistProjections(lastPlaylistId, size);
        if(playlistProjections.isEmpty()) throw new CustomException(ErrorCode.PLAYLIST_NOT_FOUND);

        List<GetSimpleMovieProjection> simpleMovieProjections = playlistManager.getSimpleMovieProjections(playlistProjections);
        if(simpleMovieProjections.isEmpty()) throw new CustomException(ErrorCode.PLAYLIST_MOVIE_NOT_FOUND);

        Map<Long, List<GetSimpleMovieResp>> moviesGroupedByPlaylist = playlistDtoMapper.mapMoviesToPlaylists(simpleMovieProjections);

        List<GetPlaylistResp> playlistRespList = playlistDtoMapper.mapPlaylists(playlistProjections, moviesGroupedByPlaylist);
        if(playlistRespList.isEmpty()) throw new CustomException(ErrorCode.PLAYLIST_NOT_FOUND);

        return new SliceImpl<>(playlistRespList);
    }

    @Override
    public AddPlaylistResp addPlaylist(AddPlaylistReq addPlaylistReq) {
        if(addPlaylistReq.movieIds().isEmpty() || addPlaylistReq.title().isEmpty()) throw new CustomException(ErrorCode.PLAYLIST_CREATE_FAILED);
        return playlistDtoMapper.toAddPlaylistResp(playlistManager.addPlaylist(addPlaylistReq));
    }

    @Override
    @Transactional
    public UpdatePlaylistResp updatePlaylist(UpdatePlaylistReq updatePlaylistReq) {
        if(updatePlaylistReq.playlistId() == null || updatePlaylistReq.movieIds().isEmpty() || updatePlaylistReq.title().isEmpty()) throw new CustomException(ErrorCode.PLAYLIST_UPDATE_FAILED);
        return playlistDtoMapper.toUpdatePlaylistResp(playlistManager.updatePlaylist(updatePlaylistReq));
    }

    @Override
    public SuccessCode deletePlaylist(Long playlistId) {
        return playlistManager.deletePlaylist(playlistId);
    }
}


