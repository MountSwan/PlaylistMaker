package com.practicum.playlistmaker.player.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.player.domain.MediaPlayerInteractor
import com.practicum.playlistmaker.player.domain.models.MediaPlayerState
import com.practicum.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerViewModel(
    private val savedTrack: Track,
    private val mediaPlayerInteractor: MediaPlayerInteractor,
) :
    ViewModel() {

    val mainThreadHandler: Handler

    private val savedTrackLiveData =
        MutableLiveData<Track>()

    fun observeSavedTrack(): LiveData<Track> = savedTrackLiveData

    private val playerStateLiveData =
        MutableLiveData<MediaPlayerState>()

    fun observePlayerState(): LiveData<MediaPlayerState> = playerStateLiveData

    private val timePlayTrackLiveData = MutableLiveData<String>()
    fun observeTimePlayTrack(): LiveData<String> = timePlayTrackLiveData

    private val timerRunnable = createUpdateTimerTask()
    private val listenerPlayerStateRunnable = listenerPlayerState()

    init {
        mainThreadHandler = Handler(Looper.getMainLooper())
        savedTrackLiveData.value = savedTrack
        mainThreadHandler.post(listenerPlayerStateRunnable)
    }

    fun defineMediaPlayerStatePreparedAsDefault() {
        mediaPlayerInteractor.defineMediaPlayerStatePreparedAsDefault()
    }

    fun preparePlayer() {
        mediaPlayerInteractor.preparePlayer(savedTrack.previewUrl)
    }

    fun playbackControl() {
        when (mediaPlayerInteractor.playerState()) {
            is MediaPlayerState.Playing -> {
                pausePlayer()
            }

            is MediaPlayerState.Prepared, MediaPlayerState.Paused -> {
                startPlayer()
            }

            is MediaPlayerState.Default -> Unit
        }
    }

    private fun startPlayer() {
        mediaPlayerInteractor.startPlayer()
        mainThreadHandler.post(timerRunnable)
    }

    fun pausePlayer() {
        mediaPlayerInteractor.pausePlayer()
        mainThreadHandler.removeCallbacks(timerRunnable)
    }

    fun releasePlayer() {
        mainThreadHandler.removeCallbacks(timerRunnable)
        mainThreadHandler.removeCallbacks(listenerPlayerStateRunnable)
        mediaPlayerInteractor.releasePlayer()
    }

    private fun listenerPlayerState(): Runnable {
        return object : Runnable {
            override fun run() {
                when (mediaPlayerInteractor.playerState()) {
                    is MediaPlayerState.Prepared.OnCompletion -> {
                        mainThreadHandler.removeCallbacks(timerRunnable)
                        playerStateLiveData.value = mediaPlayerInteractor.playerState()
                    }

                    else -> {
                        playerStateLiveData.value = mediaPlayerInteractor.playerState()
                    }
                }

                mainThreadHandler.postDelayed(this, REFRESH_TIMER_MILLIS)
            }
        }
    }

    private fun createUpdateTimerTask(): Runnable {
        return object : Runnable {
            override fun run() {
                timePlayTrackLiveData.value =
                    SimpleDateFormat(
                        "mm:ss",
                        Locale.getDefault()
                    ).format(mediaPlayerInteractor.playerCurrentPosition())
                mainThreadHandler.postDelayed(this, REFRESH_TIMER_MILLIS)
            }
        }
    }

    override fun onCleared() {
        mainThreadHandler.removeCallbacks(timerRunnable)
        mainThreadHandler.removeCallbacks(listenerPlayerStateRunnable)
        super.onCleared()
    }

    companion object {
        private const val REFRESH_TIMER_MILLIS = 500L
    }

}