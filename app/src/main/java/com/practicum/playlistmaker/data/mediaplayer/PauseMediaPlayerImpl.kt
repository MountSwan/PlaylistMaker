package com.practicum.playlistmaker.data.mediaplayer

import android.media.MediaPlayer
import com.practicum.playlistmaker.domain.PauseMediaPlayer
import com.practicum.playlistmaker.domain.models.Parameters
import com.practicum.playlistmaker.domain.usecases.SetIVControlPlayUseCase

class PauseMediaPlayerImpl(
    private val mediaPlayer: MediaPlayer,
    private val params: Parameters
) : PauseMediaPlayer {
    override fun execute() {
        mediaPlayer.pause()
        SetIVControlPlayUseCase(params.setIVControlPlay).execute(params.imageCodePlayForIVControlPlay)
        params.mediaPlayerState.playerState = params.mediaPlayerState.paused
        params.mainThreadHandler.removeCallbacks(params.timerRunnable)
    }
}