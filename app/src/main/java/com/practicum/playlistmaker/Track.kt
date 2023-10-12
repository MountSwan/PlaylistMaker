package com.practicum.playlistmaker


data class Track (
    var trackId: Long,
    var trackName: String,
    var artistName: String,
    var trackTimeMillis: Long,
    var artworkUrl100: String,
    var collectionName: String,
    var releaseDate: String,
    var primaryGenreName: String,
    var country: String
        )