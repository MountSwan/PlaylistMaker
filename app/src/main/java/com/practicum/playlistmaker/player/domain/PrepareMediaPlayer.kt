package com.practicum.playlistmaker.player.domain

interface PrepareMediaPlayer {
    fun execute(urlForPlaying: String?)
    fun defineMediaPlayerStatePreparedAsDefault()
}