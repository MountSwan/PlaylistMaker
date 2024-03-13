package com.practicum.playlistmaker.library.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_table")
data class PlaylistEntity(
    @PrimaryKey
    var playlistId: Int,
    var playlistName: String,
    var playlistDescription: String,
    var listOfTracksId: String,
    var numberOfTracksInPlaylist: Int,
)
