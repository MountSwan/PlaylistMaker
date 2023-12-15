package com.practicum.playlistmaker.player.ui

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.player.domain.models.MediaPlayerState
import com.practicum.playlistmaker.player.domain.usecases.GetMediaPlayerCurrentPositionUseCase
import com.practicum.playlistmaker.player.domain.usecases.GetMediaPlayerStateUseCase
import com.practicum.playlistmaker.player.domain.usecases.PauseMediaPlayerUseCase
import com.practicum.playlistmaker.player.domain.usecases.PrepareMediaPlayerUseCase
import com.practicum.playlistmaker.player.domain.usecases.ReleaseMediaPlayerUseCase
import com.practicum.playlistmaker.player.domain.usecases.StartMediaPlayerUseCase
import com.practicum.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerViewModel(
    private val mainThreadHandler: Handler,
    private val savedTrack: Track?,
    private val preparePlayer: PrepareMediaPlayerUseCase,
    private val pausePlayer: PauseMediaPlayerUseCase,
    private val startPlayer: StartMediaPlayerUseCase,
    private val releasePlayer: ReleaseMediaPlayerUseCase,
    private val getPlayerCurrentPosition: GetMediaPlayerCurrentPositionUseCase,
    private val getPlayerState: GetMediaPlayerStateUseCase,
) :
    ViewModel() {

    private val savedTrackLiveData =
        MutableLiveData<Track?>()
    fun observeSavedTrack(): LiveData<Track?> = savedTrackLiveData

    private val playerStateLiveData =
        MutableLiveData<MediaPlayerState>()
    fun observePlayerState(): LiveData<MediaPlayerState> = playerStateLiveData

    private val timePlayTrackLiveData = MutableLiveData<String>()
    fun observeTimePlayTrack(): LiveData<String> = timePlayTrackLiveData

    private val timerRunnable = createUpdateTimerTask()
    private val listenerPlayerStateRunnable = listenerPlayerState()

    init {
        savedTrackLiveData.value = savedTrack
        mainThreadHandler.post(listenerPlayerStateRunnable)
    }

    fun defineMediaPlayerStatePreparedAsDefault() {
        preparePlayer.defineMediaPlayerStatePreparedAsDefault()
    }

    fun preparePlayer() {
        preparePlayer.execute(savedTrack?.previewUrl)
    }

    fun playbackControl() {
        when (getPlayerState.execute()) {
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
        startPlayer.execute()
        mainThreadHandler.post(timerRunnable)
    }

    fun pausePlayer() {
        pausePlayer.execute()
        mainThreadHandler.removeCallbacks(timerRunnable)
    }

    fun releasePlayer() {
        mainThreadHandler.removeCallbacks(timerRunnable)
        mainThreadHandler.removeCallbacks(listenerPlayerStateRunnable)
        releasePlayer.execute()
    }

    private fun listenerPlayerState(): Runnable {
        return object : Runnable {
            override fun run() {
                when (getPlayerState.execute()) {
                    is MediaPlayerState.Prepared.OnCompletion -> {
                        mainThreadHandler.removeCallbacks(timerRunnable)
                        playerStateLiveData.value = getPlayerState.execute()
                    }

                    else -> {
                        playerStateLiveData.value = getPlayerState.execute()
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
                    ).format(getPlayerCurrentPosition.execute())
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
        const val REFRESH_TIMER_MILLIS = 500L
    }

}