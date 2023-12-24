package com.practicum.playlistmaker.player.data

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.domain.AudioPlayer
import com.practicum.playlistmaker.player.domain.models.MediaPlayerState


class AudioPlayerImpl(private val mediaPlayer: MediaPlayer) : AudioPlayer {
    private var mediaPlayerState: MediaPlayerState = MediaPlayerState.Default

    override fun preparePlayer(
        urlForPlaying: String?,
    ) {
        mediaPlayer.setDataSource(urlForPlaying)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            mediaPlayerState = MediaPlayerState.Prepared.OnPrepared
        }
        mediaPlayer.setOnCompletionListener {
            mediaPlayerState = MediaPlayerState.Prepared.OnCompletion
        }
    }

    override fun startPlayer() {
        mediaPlayerState = MediaPlayerState.Playing
        mediaPlayer.start()
    }

    override fun pausePlayer() {
        mediaPlayerState = MediaPlayerState.Paused
        mediaPlayer.pause()
    }

    override fun releasePlayer() {
        //mediaPlayer.release()
        mediaPlayer.reset()
    }

    override fun playerCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    override fun playerState(): MediaPlayerState {
        return mediaPlayerState
    }

    override fun defineMediaPlayerStatePreparedAsDefault() {
        mediaPlayerState = MediaPlayerState.Prepared.Default
    }

}