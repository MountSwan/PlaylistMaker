package com.practicum.playlistmaker.library.ui

import java.io.File

data class PlaylistUI(
    val playlistId: Int,
    val playlistName: String,
    val playlistDescription: String,
    val listOfTracksId: String,
    val numberOfTracksInPlaylist: Int,
    val coverImage: File,
)
