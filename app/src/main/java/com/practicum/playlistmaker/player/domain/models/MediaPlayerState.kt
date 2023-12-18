package com.practicum.playlistmaker.player.domain.models

sealed interface MediaPlayerState {
    object Default : MediaPlayerState
    sealed class Prepared : MediaPlayerState {
        object Default: Prepared()
        object OnPrepared: Prepared()
        object OnCompletion: Prepared()
    }
    object Playing : MediaPlayerState
    object Paused : MediaPlayerState
}