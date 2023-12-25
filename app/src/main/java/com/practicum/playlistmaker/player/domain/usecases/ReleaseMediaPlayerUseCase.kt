package com.practicum.playlistmaker.player.domain.usecases

import com.practicum.playlistmaker.player.domain.AudioPlayer
import com.practicum.playlistmaker.player.domain.ReleaseMediaPlayer

class ReleaseMediaPlayerUseCase(private val audioPlayer: AudioPlayer): ReleaseMediaPlayer {

    override fun execute() {
        audioPlayer.releasePlayer()
    }

}