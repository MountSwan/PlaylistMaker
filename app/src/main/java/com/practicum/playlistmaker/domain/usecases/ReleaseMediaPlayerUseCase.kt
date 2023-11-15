package com.practicum.playlistmaker.domain.usecases

import com.practicum.playlistmaker.domain.ReleaseMediaPlayer

class ReleaseMediaPlayerUseCase(private val releaseMediaPlayer: ReleaseMediaPlayer) {

    fun execute() {
        releaseMediaPlayer.execute()
    }

}