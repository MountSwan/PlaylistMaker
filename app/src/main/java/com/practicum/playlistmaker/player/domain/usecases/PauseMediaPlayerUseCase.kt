package com.practicum.playlistmaker.player.domain.usecases

import com.practicum.playlistmaker.player.domain.AudioPlayer

class PauseMediaPlayerUseCase(private val audioPlayer: AudioPlayer) {
    fun execute() {
        audioPlayer.pausePlayer()
    }
}