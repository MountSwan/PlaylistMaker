package com.practicum.playlistmaker.library.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_track_table")
data class FavoriteTrackEntity(
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
