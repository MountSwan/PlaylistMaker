package com.practicum.playlistmaker.presentation

import android.widget.ImageView
import android.widget.TextView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.AudioPlayer
import com.practicum.playlistmaker.domain.MediaPlayerListener
import com.practicum.playlistmaker.domain.models.MediaPlayerState

class MediaPlayerListenerImpl(
    private val audioPlayer: AudioPlayer, private val controlPlay: ImageView,
    private val timePlayTrack: TextView, private val mediaPlayerState: MediaPlayerState
) :
    MediaPlayerListener {

    init {
        audioPlayer.addListener(this)
    }

    override fun onCompletionMediaPlayer() {
        controlPlay.setImageResource(R.drawable.control_play)
        mediaPlayerState.startTime = 0L
        timePlayTrack.text = "00:00"
    }

    override fun onPreparedMediaPlayer() {
        controlPlay.isEnabled = true
    }
}