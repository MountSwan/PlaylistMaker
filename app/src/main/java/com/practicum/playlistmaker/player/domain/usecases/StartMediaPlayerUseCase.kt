package com.practicum.playlistmaker.player.domain.usecases

import com.practicum.playlistmaker.player.domain.AudioPlayer
import com.practicum.playlistmaker.player.domain.StartMediaPlayer

class StartMediaPlayerUseCase(private val audioPlayer: AudioPlayer): StartMediaPlayer {
    override fun execute() {
        audioPlayer.startPlayer()
    }
}