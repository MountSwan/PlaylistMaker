package com.practicum.playlistmaker.domain

import com.practicum.playlistmaker.domain.models.MediaPlayerState


interface AudioPlayer {

    fun preparePlayer(urlForPlaying: String?)

    fun startPlayer()

    fun pausePlayer()

    fun releasePlayer()

    fun playerCurrentPosition(): Int

    fun playerState(): MediaPlayerState

    fun addListener(listener: MediaPlayerListener)

}