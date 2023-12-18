package com.practicum.playlistmaker.player.domain.usecases

import com.practicum.playlistmaker.player.domain.AudioPlayer

class ReleaseMediaPlayerUseCase(private val audioPlayer: AudioPlayer) {

    fun execute() {
        audioPlayer.releasePlayer()
    }

}