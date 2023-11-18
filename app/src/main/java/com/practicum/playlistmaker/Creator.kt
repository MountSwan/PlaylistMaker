package com.practicum.playlistmaker

import android.os.Handler
import com.practicum.playlistmaker.data.repository.AudioPlayerImpl
import com.practicum.playlistmaker.domain.AudioPlayer
import com.practicum.playlistmaker.domain.models.MediaPlayerState

object Creator {

    fun provideAudioPlayer(
        mediaPlayerState: MediaPlayerState,
        mainThreadHandler: Handler,
        timerRunnable: Runnable
    ): AudioPlayer {
        return AudioPlayerImpl(
            mediaPlayerState, mainThreadHandler, timerRunnable
        )
    }

}