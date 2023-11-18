package com.practicum.playlistmaker.domain.models


data class MediaPlayerState(
    val default: String = "STATE_DEFAULT",
    val prepared: String = "STATE_PREPARED",
    val playing: String = "STATE_PLAYING",
    val paused: String = "STATE_PAUSED",
    var playerState: String = "STATE_DEFAULT",
    var startTime: Long = 0L,
    var endTime: Long = 0L
)
