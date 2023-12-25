package com.practicum.playlistmaker.player.ui

import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.SAVE_TRACK_FOR_AUDIO_PLAYER_KEY
import com.practicum.playlistmaker.player.domain.GetMediaPlayerCurrentPosition
import com.practicum.playlistmaker.player.domain.GetMediaPlayerState
import com.practicum.playlistmaker.player.domain.PauseMediaPlayer
import com.practicum.playlistmaker.player.domain.PrepareMediaPlayer
import com.practicum.playlistmaker.player.domain.ReleaseMediaPlayer
import com.practicum.playlistmaker.player.domain.StartMediaPlayer
import com.practicum.playlistmaker.player.domain.models.MediaPlayerState
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.models.TrackUi
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerViewModel(
    private val preparePlayer: PrepareMediaPlayer,
    private val pausePlayer: PauseMediaPlayer,
    private val startPlayer: StartMediaPlayer,
    private val releasePlayer: ReleaseMediaPlayer,
    private val getPlayerCurrentPosition: GetMediaPlayerCurrentPosition,
    private val getPlayerState: GetMediaPlayerState,
) :
    ViewModel() {

    val mainThreadHandler: Handler

    private val savedTrack: Track = Track(
        trackId = 0L,
        trackName = "",
        artistName = "",
        trackTimeMillis = 0L,
        trackTime = "",
        artworkUrl100 = "",
        artworkUrl512 = "",
        collectionName = "",
        releaseDate = "",
        primaryGenreName = "",
        country = "",
        previewUrl = ""
    )

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
        mainThreadHandler = Handler(Looper.getMainLooper())
    }

    fun getSavedTrack(intent: Intent) {
        val savedTrackUi = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(SAVE_TRACK_FOR_AUDIO_PLAYER_KEY, TrackUi::class.java)
        } else {
            intent.getParcelableExtra(SAVE_TRACK_FOR_AUDIO_PLAYER_KEY)
        }
        savedTrack.apply {
            trackId = savedTrackUi?.trackId!!
            trackName = savedTrackUi.trackName
            artistName = savedTrackUi.artistName
            trackTimeMillis = savedTrackUi.trackTimeMillis
            trackTime = savedTrackUi.trackTime
            artworkUrl100 = savedTrackUi.artworkUrl100
            artworkUrl512 = savedTrackUi.artworkUrl512
            collectionName = savedTrackUi.collectionName
            releaseDate = savedTrackUi.releaseDate
            primaryGenreName = savedTrackUi.primaryGenreName
            country = savedTrackUi.country
            previewUrl = savedTrackUi.previewUrl
        }
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