package com.practicum.playlistmaker.data.mediaplayer

import android.media.MediaPlayer
import com.practicum.playlistmaker.domain.StartMediaPlayer
import com.practicum.playlistmaker.domain.models.Parameters
import com.practicum.playlistmaker.domain.usecases.SetIVControlPlayUseCase

class StartMediaPlayerImpl(
    private val mediaPlayer: MediaPlayer,
    private val params: Parameters
): StartMediaPlayer {
    override fun execute() {
        mediaPlayer.start()
        SetIVControlPlayUseCase(params.setIVControlPlay).execute(params.imageCodePauseForIVControlPlay)
        params.mediaPlayerState.playerState = params.mediaPlayerState.playing
        params.mainThreadHandler.post(params.timerRunnable)
    }
}