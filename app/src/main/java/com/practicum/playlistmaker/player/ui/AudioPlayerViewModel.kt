package com.practicum.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.player.domain.MediaPlayerInteractor
import com.practicum.playlistmaker.player.domain.models.MediaPlayerState
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerViewModel(
    private val savedTrack: Track?,
    private val mediaPlayerInteractor: MediaPlayerInteractor,
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

    private var timerJob: Job? = null
    private var listenerPlayerStateJob: Job? = null

    init {
        savedTrackLiveData.value = savedTrack
        listenerPlayerStateJob = viewModelScope.launch {
            while (true) {
                delay(REFRESH_TIMER_MILLIS)
                when (mediaPlayerInteractor.playerState()) {
                    is MediaPlayerState.Prepared.OnCompletion -> {
                        timerJob?.cancel()
                        playerStateLiveData.value = mediaPlayerInteractor.playerState()
                    }

                    else -> {
                        playerStateLiveData.value = mediaPlayerInteractor.playerState()
                    }
                }
            }
        }
    }

    fun defineMediaPlayerStatePreparedAsDefault() {
        mediaPlayerInteractor.defineMediaPlayerStatePreparedAsDefault()
    }

    fun preparePlayer() {
        mediaPlayerInteractor.preparePlayer(savedTrack?.previewUrl)
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
        timerJob = viewModelScope.launch {
            while (mediaPlayerInteractor.playerState() == MediaPlayerState.Playing) {
                delay(REFRESH_TIMER_MILLIS)
                timePlayTrackLiveData.value =
                    SimpleDateFormat(
                        "mm:ss",
                        Locale.getDefault()
                    ).format(mediaPlayerInteractor.playerCurrentPosition())
            }
        }

    }

    fun pausePlayer() {
        mediaPlayerInteractor.pausePlayer()
        timerJob?.cancel()
    }

    fun releasePlayer() {
        timerJob?.cancel()
        listenerPlayerStateJob?.cancel()
        mediaPlayerInteractor.releasePlayer()
    }

    companion object {
        private const val REFRESH_TIMER_MILLIS = 300L
    }

}