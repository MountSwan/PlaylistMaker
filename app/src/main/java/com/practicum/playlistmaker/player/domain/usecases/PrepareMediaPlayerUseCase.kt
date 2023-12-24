package com.practicum.playlistmaker.player.domain.usecases

import com.practicum.playlistmaker.player.domain.AudioPlayer
import com.practicum.playlistmaker.player.domain.PrepareMediaPlayer

class PrepareMediaPlayerUseCase(private val audioPlayer: AudioPlayer): PrepareMediaPlayer {
    override fun execute(urlForPlaying: String?) {
        audioPlayer.preparePlayer(urlForPlaying)
    }

    override fun defineMediaPlayerStatePreparedAsDefault() {
        audioPlayer.defineMediaPlayerStatePreparedAsDefault()
    }

}