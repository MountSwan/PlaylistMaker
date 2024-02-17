package com.practicum.playlistmaker.search.domain.models


data class Track (
    var dataBaseId: Int? = 0,
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
    var isFavorite: Boolean? = false
        )



