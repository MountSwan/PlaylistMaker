package com.practicum.playlistmaker.player.domain.usecases

import com.practicum.playlistmaker.player.domain.AudioPlayer

class StartMediaPlayerUseCase(private val audioPlayer: AudioPlayer) {
    fun execute() {
        audioPlayer.startPlayer()
    }
}