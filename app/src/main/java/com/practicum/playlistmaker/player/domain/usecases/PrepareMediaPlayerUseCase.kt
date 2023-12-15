package com.practicum.playlistmaker.player.domain.usecases

import com.practicum.playlistmaker.player.domain.AudioPlayer

class PrepareMediaPlayerUseCase(private val audioPlayer: AudioPlayer) {
    fun execute(urlForPlaying: String?) {
        audioPlayer.preparePlayer(urlForPlaying)
    }

    fun defineMediaPlayerStatePreparedAsDefault() {
        audioPlayer.defineMediaPlayerStatePreparedAsDefault()
    }

}