package com.practicum.playlistmaker.domain.models

sealed interface MediaPlayerState {
    object Default : MediaPlayerState
    object Prepared : MediaPlayerState
    object Playing : MediaPlayerState
    object Paused : MediaPlayerState
}