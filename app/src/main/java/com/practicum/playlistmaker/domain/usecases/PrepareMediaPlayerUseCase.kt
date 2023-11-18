package com.practicum.playlistmaker.domain.usecases

import com.practicum.playlistmaker.domain.AudioPlayer

class PrepareMediaPlayerUseCase(private val audioPlayer: AudioPlayer) {
    fun execute(urlForPlaying: String?) {
        audioPlayer.preparePlayer(urlForPlaying)
    }
}