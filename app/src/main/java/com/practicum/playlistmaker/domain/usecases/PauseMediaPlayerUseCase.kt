package com.practicum.playlistmaker.domain.usecases

import com.practicum.playlistmaker.domain.PauseMediaPlayer

class PauseMediaPlayerUseCase(private val pauseMediaPlayer: PauseMediaPlayer) {
    fun execute() {
        pauseMediaPlayer.execute()
    }
}