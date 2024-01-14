package com.practicum.playlistmaker.player.domain

import com.practicum.playlistmaker.player.domain.models.MediaPlayerState

class MediaPlayerInteractorImpl(private val audioPlayer: AudioPlayer) : MediaPlayerInteractor {

    override fun preparePlayer(urlForPlaying: String) {
        audioPlayer.preparePlayer(urlForPlaying)
    }

    override fun startPlayer() {
        audioPlayer.startPlayer()
    }

    override fun pausePlayer() {
        audioPlayer.pausePlayer()
    }

    override fun releasePlayer() {
        audioPlayer.releasePlayer()
    }

    override fun playerCurrentPosition(): Int {
        return audioPlayer.playerCurrentPosition()
    }

    override fun playerState(): MediaPlayerState {
        return audioPlayer.playerState()
    }

    override fun defineMediaPlayerStatePreparedAsDefault() {
        audioPlayer.defineMediaPlayerStatePreparedAsDefault()
    }

}