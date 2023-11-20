package com.practicum.playlistmaker.domain.usecases

import com.practicum.playlistmaker.domain.AudioPlayer

class ReleaseMediaPlayerUseCase(private val audioPlayer: AudioPlayer) {

    fun execute() {
        audioPlayer.releasePlayer()
    }

}