package com.ureca.picky_be.base.business.playlist.dto;

import java.util.List;

public record AddPlaylistReq(List<Long> movieIds, String title) {
}
