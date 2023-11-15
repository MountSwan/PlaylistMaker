package com.practicum.playlistmaker.domain.usecases

import com.practicum.playlistmaker.domain.PauseMediaPlayer
import com.practicum.playlistmaker.domain.StartMediaPlayer
import com.practicum.playlistmaker.domain.models.MediaPlayerState

class PlaybackControlUseCase(private val pauseMediaPlayer: PauseMediaPlayer, private val startMediaPlayer: StartMediaPlayer) {
    fun execute(mediaPlayerState: MediaPlayerState) {
        when (mediaPlayerState.playerState) {
            mediaPlayerState.playing -> {
                pauseMediaPlayer.execute()
            }

            mediaPlayerState.prepared, mediaPlayerState.paused -> {
                startMediaPlayer.execute()
            }
        }
    }
}