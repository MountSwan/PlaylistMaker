package com.practicum.playlistmaker.player.domain.usecases

import com.practicum.playlistmaker.player.domain.AudioPlayer
import com.practicum.playlistmaker.player.domain.PauseMediaPlayer

class PauseMediaPlayerUseCase(private val audioPlayer: AudioPlayer): PauseMediaPlayer {
    override fun execute() {
        audioPlayer.pausePlayer()
    }
}