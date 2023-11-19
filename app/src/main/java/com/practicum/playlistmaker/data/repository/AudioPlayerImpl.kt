package com.practicum.playlistmaker.data.repository

import android.media.MediaPlayer
import com.practicum.playlistmaker.domain.AudioPlayer
import com.practicum.playlistmaker.domain.MediaPlayerListener
import com.practicum.playlistmaker.domain.models.MediaPlayerState
import java.lang.ref.WeakReference

class AudioPlayerImpl : AudioPlayer {
    private var mediaPlayer = MediaPlayer()
    private var listener = WeakReference<MediaPlayerListener>(null)
    private var mediaPlayerState: MediaPlayerState = MediaPlayerState.Default

    override fun preparePlayer(urlForPlaying: String?) {
        mediaPlayer.setDataSource(urlForPlaying)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            mediaPlayerState = MediaPlayerState.Prepared
            listener.get()?.onPreparedMediaPlayer()
        }
        mediaPlayer.setOnCompletionListener {
            mediaPlayerState = MediaPlayerState.Prepared
            listener.get()?.onCompletionMediaPlayer()
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()
        mediaPlayerState = MediaPlayerState.Playing
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
        mediaPlayerState = MediaPlayerState.Paused
    }

    override fun releasePlayer() {
        mediaPlayer.release()
    }

    override fun playerCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    override fun playerState(): MediaPlayerState {
        return mediaPlayerState
    }

    override fun addListener(listener: MediaPlayerListener) {
        this.listener = WeakReference(listener)
    }

}