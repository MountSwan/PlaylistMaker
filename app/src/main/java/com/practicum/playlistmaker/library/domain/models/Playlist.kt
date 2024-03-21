package com.practicum.playlistmaker.library.domain.models

data class Playlist(
    var playlistId: Int,
    var playlistName: String,
    var playlistDescription: String,
    var listOfTracksId: String,
    var numberOfTracksInPlaylist: Int,
)
