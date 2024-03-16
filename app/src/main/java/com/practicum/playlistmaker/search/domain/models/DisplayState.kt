package com.practicum.playlistmaker.search.domain.models

sealed interface DisplayState {
    object TracksSearch : DisplayState
    object SearchHistory : DisplayState
    object PlaceholdersWithoutRefreshButton : DisplayState
    object PlaceholdersWithRefreshButton : DisplayState
}