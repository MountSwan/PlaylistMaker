package com.practicum.playlistmaker.player.domain.usecases

import com.practicum.playlistmaker.player.domain.AudioPlayer
import com.practicum.playlistmaker.player.domain.GetMediaPlayerState
import com.practicum.playlistmaker.player.domain.models.MediaPlayerState

class GetMediaPlayerStateUseCase(private val audioPlayer: AudioPlayer) : GetMediaPlayerState {
    override fun execute(): MediaPlayerState {
        return audioPlayer.playerState()
    }
}