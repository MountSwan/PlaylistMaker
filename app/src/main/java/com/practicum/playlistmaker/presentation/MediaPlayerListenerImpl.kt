package com.practicum.playlistmaker.presentation

import android.os.Handler
import android.widget.ImageView
import android.widget.TextView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.AudioPlayer
import com.practicum.playlistmaker.domain.MediaPlayerListener

class MediaPlayerListenerImpl(
    audioPlayer: AudioPlayer,
    private val controlPlay: ImageView,
    private val timePlayTrack: TextView,
    private val mainThreadHandler: Handler,
    private val timerRunnable: Runnable
) :
    MediaPlayerListener {

    init {
        audioPlayer.addListener(this)
    }

    override fun onCompletionMediaPlayer() {
        mainThreadHandler.removeCallbacks(timerRunnable)
        controlPlay.setImageResource(R.drawable.control_play)
        timePlayTrack.text = "00:00"
    }

    override fun onPreparedMediaPlayer() {
        controlPlay.isEnabled = true
    }
}