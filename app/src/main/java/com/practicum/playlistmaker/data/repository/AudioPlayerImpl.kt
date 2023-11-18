package com.practicum.playlistmaker.data.repository

import android.media.MediaPlayer
import android.os.Handler
import com.practicum.playlistmaker.domain.AudioPlayer
import com.practicum.playlistmaker.domain.MediaPlayerListener
import com.practicum.playlistmaker.domain.models.MediaPlayerState
import java.lang.ref.WeakReference

class AudioPlayerImpl(
    private val mediaPlayerState: MediaPlayerState,
    private val mainThreadHandler: Handler,
    private val timerRunnable: Runnable
) : AudioPlayer {
    private var mediaPlayer = MediaPlayer()
    private var listener = WeakReference<MediaPlayerListener>(null)

    override fun preparePlayer(urlForPlaying: String?) {
        mediaPlayer.setDataSource(urlForPlaying)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            mediaPlayerState.playerState = mediaPlayerState.prepared
            listener.get()?.onPreparedMediaPlayer()
        }
        mediaPlayer.setOnCompletionListener {
            mediaPlayerState.playerState = mediaPlayerState.prepared
            mainThreadHandler.removeCallbacks(timerRunnable)
            listener.get()?.onCompletionMediaPlayer()
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()
        mediaPlayerState.playerState = mediaPlayerState.playing
        if (mediaPlayerState.startTime == 0L) {
            mediaPlayerState.startTime = System.currentTimeMillis()
        } else {
            mediaPlayerState.startTime += System.currentTimeMillis() - mediaPlayerState.endTime
        }
        mainThreadHandler.post(timerRunnable)
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
        mediaPlayerState.playerState = mediaPlayerState.paused
        mainThreadHandler.removeCallbacks(timerRunnable)
        mediaPlayerState.endTime = System.currentTimeMillis()
    }

    override fun releasePlayer() {
        mainThreadHandler.removeCallbacks(timerRunnable)
        mediaPlayer.release()
    }

    override fun addListener(listener: MediaPlayerListener) {
        this.listener = WeakReference(listener)
    }

}