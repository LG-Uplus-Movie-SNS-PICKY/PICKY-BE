package com.ureca.picky_be.base.business.playlist.dto;

import java.util.List;

public record UpdatePlaylistReq(Long playlistId, List<Long> movieIds, String title) {
}
