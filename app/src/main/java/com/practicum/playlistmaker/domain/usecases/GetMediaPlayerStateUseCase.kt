package com.practicum.playlistmaker.domain.usecases

import com.practicum.playlistmaker.domain.AudioPlayer
import com.practicum.playlistmaker.domain.models.MediaPlayerState

class GetMediaPlayerStateUseCase(private val audioPlayer: AudioPlayer) {
    fun execute(): MediaPlayerState {
        return audioPlayer.playerState()
    }
}