package com.practicum.playlistmaker.domain.usecases

import com.practicum.playlistmaker.domain.AudioPlayer

class GetMediaPlayerCurrentPositionUseCase(private val audioPlayer: AudioPlayer) {
    fun execute(): Int {
        return audioPlayer.playerCurrentPosition()
    }
}