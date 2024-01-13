package com.practicum.playlistmaker.player.domain

import com.practicum.playlistmaker.player.domain.models.MediaPlayerState

interface MediaPlayerInteractor {

    fun preparePlayer(urlForPlaying: String?)

    fun startPlayer()

    fun pausePlayer()

    fun releasePlayer()

    fun playerCurrentPosition(): Int

    fun playerState(): MediaPlayerState

    fun defineMediaPlayerStatePreparedAsDefault()

}