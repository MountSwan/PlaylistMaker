package com.practicum.playlistmaker.player.domain.usecases

import com.practicum.playlistmaker.player.domain.AudioPlayer

class GetMediaPlayerCurrentPositionUseCase(private val audioPlayer: AudioPlayer) {
    fun execute(): Int {
        return audioPlayer.playerCurrentPosition()
    }
}