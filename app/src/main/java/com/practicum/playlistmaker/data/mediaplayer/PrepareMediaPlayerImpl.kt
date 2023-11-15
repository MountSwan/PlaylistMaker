package com.practicum.playlistmaker.data.mediaplayer

import android.media.MediaPlayer
import com.practicum.playlistmaker.domain.PrepareMediaPlayer
import com.practicum.playlistmaker.domain.models.Parameters
import com.practicum.playlistmaker.domain.usecases.SetIVControlPlayUseCase

const val BEGINNING_TIME_PLAY = "00:00"

class PrepareMediaPlayerImpl(
    private val mediaPlayer: MediaPlayer,
    private val params: Parameters
) : PrepareMediaPlayer {

    override fun preparePlayer() {
        mediaPlayer.setDataSource(params.urlForPlaying)
        mediaPlayer.prepareAsync()

        mediaPlayer.setOnPreparedListener {
            params.enableIVControlPlay.execute()
            params.mediaPlayerState.playerState = params.mediaPlayerState.prepared
        }

        mediaPlayer.setOnCompletionListener {
            SetIVControlPlayUseCase(params.setIVControlPlay).execute(params.imageCodePlayForIVControlPlay)
            params.mediaPlayerState.playerState = params.mediaPlayerState.prepared
            params.mainThreadHandler.removeCallbacks(params.timerRunnable)
            params.setTVTimePlayTrackText.execute(BEGINNING_TIME_PLAY)
        }
    }

}