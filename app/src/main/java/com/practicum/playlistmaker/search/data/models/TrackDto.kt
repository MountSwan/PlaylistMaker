package com.practicum.playlistmaker.search.data.models

import java.text.SimpleDateFormat
import java.util.Locale

data class TrackDto(
    var trackId: Long,
    var trackName: String,
    var artistName: String,
    var trackTimeMillis: Long,
    var artworkUrl100: String,
    var collectionName: String,
    var releaseDate: String,
    var primaryGenreName: String,
    var country: String,
    var previewUrl: String
) {

    val artworkUrl512
        get() = if (artworkUrl100.isNotEmpty()) {
            artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
        } else {
            ""
        }

    val trackTime: String
        get() = SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis)

}