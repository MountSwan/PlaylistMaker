package com.practicum.playlistmaker.domain.models


data class MediaPlayerState(
    val default: Int = 0,
    val prepared: Int = 1,
    val playing: Int = 2,
    val paused: Int = 3,
    var playerState: Int = 0
)
