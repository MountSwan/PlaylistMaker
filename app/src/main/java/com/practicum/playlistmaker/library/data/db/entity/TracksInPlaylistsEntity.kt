package com.practicum.playlistmaker.library.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracks_in_playlists_table")
data class TracksInPlaylistsEntity(
    @PrimaryKey
    var dataBaseId: Int?,
    var trackId: Long?,
    var trackName: String?,
    var artistName: String?,
    var trackTimeMillis: Long?,
    var trackTime: String?,
    var artworkUrl100: String?,
    var artworkUrl512: String?,
    var collectionName: String?,
    var releaseDate: String?,
    var primaryGenreName: String?,
    var country: String?,
    var previewUrl: String?,
    var isFavorite: Boolean?,
)
