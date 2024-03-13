package com.practicum.playlistmaker.library.data.db.converters

import com.practicum.playlistmaker.library.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.library.domain.models.Playlist

class PlaylistDbConvertor {

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlist.playlistId,
            playlist.playlistName,
            playlist.playlistDescription,
            playlist.listOfTracksId,
            playlist.numberOfTracksInPlaylist,
        )
    }

    fun map(playlist: PlaylistEntity): Playlist {
        return Playlist(
            playlist.playlistId,
            playlist.playlistName,
            playlist.playlistDescription,
            playlist.listOfTracksId,
            playlist.numberOfTracksInPlaylist,
        )
    }

}