package com.practicum.playlistmaker.data.mediaplayer

import android.media.MediaPlayer
import com.practicum.playlistmaker.domain.ReleaseMediaPlayer
import com.practicum.playlistmaker.domain.models.Parameters

class ReleaseMediaPlayerImpl(
    private val mediaPlayer: MediaPlayer,
    private val params: Parameters
) : ReleaseMediaPlayer {

    override fun execute() {
        params.mainThreadHandler.removeCallbacks(params.timerRunnable)
        mediaPlayer.release()
    }

}