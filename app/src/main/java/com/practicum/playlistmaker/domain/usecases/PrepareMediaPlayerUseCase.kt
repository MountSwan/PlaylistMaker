package com.practicum.playlistmaker.domain.usecases

import com.practicum.playlistmaker.domain.PrepareMediaPlayer

class PrepareMediaPlayerUseCase(private val prepareMediaPlayer: PrepareMediaPlayer) {
    fun execute() {
        prepareMediaPlayer.preparePlayer()
    }
}