package com.practicum.playlistmaker.domain.usecases

import com.practicum.playlistmaker.domain.AudioPlayer

class StartMediaPlayerUseCase(private val audioPlayer: AudioPlayer) {
    fun execute() {
        audioPlayer.startPlayer()
    }
}