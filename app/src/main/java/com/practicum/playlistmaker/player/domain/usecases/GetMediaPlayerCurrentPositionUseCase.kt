package com.practicum.playlistmaker.player.domain.usecases

import com.practicum.playlistmaker.player.domain.AudioPlayer
import com.practicum.playlistmaker.player.domain.GetMediaPlayerCurrentPosition

class GetMediaPlayerCurrentPositionUseCase(private val audioPlayer: AudioPlayer) :
    GetMediaPlayerCurrentPosition {
    override fun execute(): Int {
        return audioPlayer.playerCurrentPosition()
    }
}