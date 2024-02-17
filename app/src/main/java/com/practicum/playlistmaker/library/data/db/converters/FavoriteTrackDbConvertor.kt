package com.practicum.playlistmaker.library.data.db.converters

import com.practicum.playlistmaker.library.data.db.entity.FavoriteTrackEntity
import com.practicum.playlistmaker.search.domain.models.Track

class FavoriteTrackDbConvertor {
    fun map(track: Track?): FavoriteTrackEntity {
        return FavoriteTrackEntity(
            track?.dataBaseId,
            track?.trackId,
            track?.trackName,
            track?.artistName,
            track?.trackTimeMillis,
            track?.trackTime,
            track?.artworkUrl100,
            track?.artworkUrl512,
            track?.collectionName,
            track?.releaseDate,
            track?.primaryGenreName,
            track?.country,
            track?.previewUrl,
            track?.isFavorite,
        )
    }

    fun map(track: FavoriteTrackEntity): Track {
        return Track(
            track.dataBaseId,
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.trackTime,
            track.artworkUrl100,
            track.artworkUrl512,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl,
            track.isFavorite,
        )
    }

}