package com.practicum.playlistmaker.search.ui.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrackUi(
    var trackId: Long,
    var trackName: String,
    var artistName: String,
    var trackTimeMillis: Long,
    var trackTime: String,
    var artworkUrl100: String,
    var artworkUrl512: String,
    var collectionName: String,
    var releaseDate: String,
    var primaryGenreName: String,
    var country: String,
    var previewUrl: String
) : Parcelable
