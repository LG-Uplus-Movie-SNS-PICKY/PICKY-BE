package com.ureca.picky_be.base.implementation.mapper;

import com.ureca.picky_be.base.business.movie.dto.GetSimpleMovieProjection;
import com.ureca.picky_be.base.business.movie.dto.GetSimpleMovieResp;
import com.ureca.picky_be.base.business.playlist.dto.AddPlaylistResp;
import com.ureca.picky_be.base.business.playlist.dto.GetPlaylistProjection;
import com.ureca.picky_be.base.business.playlist.dto.GetPlaylistResp;
import com.ureca.picky_be.base.business.playlist.dto.UpdatePlaylistResp;
import com.ureca.picky_be.jpa.playlist.Playlist;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PlaylistDtoMapper {
    public Map<Long, List<GetSimpleMovieResp>> mapMoviesToPlaylists(List<GetSimpleMovieProjection> movies) {
        return movies.stream()
                .collect(Collectors.groupingBy(
                        GetSimpleMovieProjection::getPlaylistId, // Playlist ID로 그룹화
                        Collectors.mapping(
                                m -> new GetSimpleMovieResp(
                                        m.getMovieId(),
                                        m.getTitle(),
                                        m.getLikes(),
                                        m.getTotalRating(),
                                        m.getPosterUrl(),
                                        m.getBackdropUrl()
                                ),
                                Collectors.toList()
                        )
                ));
    }

    public List<GetPlaylistResp> mapPlaylists(
            Slice<GetPlaylistProjection> playlists,
            Map<Long, List<GetSimpleMovieResp>> moviesGroupedByPlaylist
    ) {
        return playlists.stream()
                .map(pl -> new GetPlaylistResp(
                        pl.getPlaylistId(),
                        pl.getTitle(),
                        moviesGroupedByPlaylist.getOrDefault(pl.getPlaylistId(), List.of()) // 없는 경우 빈 리스트 반환
                ))
                .toList();
    }

    public AddPlaylistResp toAddPlaylistResp(Playlist playlist) {
        return new AddPlaylistResp(
                playlist.getId(),
                playlist.getTitle()
        );
    }

    public UpdatePlaylistResp toUpdatePlaylistResp(Playlist playlist) {
        return new UpdatePlaylistResp(
                playlist.getId(),
                playlist.getTitle()
        );
    }

}
