package com.ureca.picky_be.base.persistence.playlist;

import com.ureca.picky_be.jpa.movie.MoviePlaylist;
import com.ureca.picky_be.jpa.playlist.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MoviePlaylistRepository extends JpaRepository<MoviePlaylist, Integer> {
    @Modifying(clearAutomatically = true)
    @Query("""
    DELETE FROM MoviePlaylist mpl
    WHERE mpl.playlist = :playlist
    """)
    void deleteByPlaylist(@Param("playlist") Playlist playlist);
}
