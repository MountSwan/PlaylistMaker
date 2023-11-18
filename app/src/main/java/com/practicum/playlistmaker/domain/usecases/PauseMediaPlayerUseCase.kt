package com.practicum.playlistmaker.domain.usecases

import com.practicum.playlistmaker.domain.AudioPlayer

class PauseMediaPlayerUseCase(private val audioPlayer: AudioPlayer) {
    fun execute() {
        audioPlayer.pausePlayer()
    }
}