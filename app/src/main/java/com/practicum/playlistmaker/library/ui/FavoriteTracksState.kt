package com.practicum.playlistmaker.library.ui

import com.practicum.playlistmaker.search.domain.models.Track

sealed interface FavoriteTracksState {
    object Empty : FavoriteTracksState
    data class Content(val tracks: ArrayList<Track>) : FavoriteTracksState
}