package com.practicum.playlistmaker.library.domain.models

data class Playlist(
    val playlistId: Int,
    val playlistName: String,
    val playlistDescription: String,
    val listOfTracksId: String,
    val numberOfTracksInPlaylist: Int,
)
