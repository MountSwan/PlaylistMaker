package com.practicum.playlistmaker.player.ui

import android.content.Intent
import android.os.Build
import android.os.Handler
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.SAVE_TRACK_FOR_AUDIO_PLAYER_KEY
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.player.domain.usecases.GetMediaPlayerCurrentPositionUseCase
import com.practicum.playlistmaker.player.domain.usecases.GetMediaPlayerStateUseCase
import com.practicum.playlistmaker.player.domain.usecases.PauseMediaPlayerUseCase
import com.practicum.playlistmaker.player.domain.usecases.PrepareMediaPlayerUseCase
import com.practicum.playlistmaker.player.domain.usecases.ReleaseMediaPlayerUseCase
import com.practicum.playlistmaker.player.domain.usecases.StartMediaPlayerUseCase
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.models.TrackUi

class AudioPlayerViewModelFactory(intent: Intent) :
    ViewModelProvider.Factory {

    private val audioPlayer = Creator.provideAudioPlayer()
    private val preparePlayer = PrepareMediaPlayerUseCase(audioPlayer)
    private val pausePlayer = PauseMediaPlayerUseCase(audioPlayer)
    private val startPlayer = StartMediaPlayerUseCase(audioPlayer)
    private val releasePlayer = ReleaseMediaPlayerUseCase(audioPlayer)
    private val getPlayerCurrentPosition = GetMediaPlayerCurrentPositionUseCase(audioPlayer)
    private val getPlayerState = GetMediaPlayerStateUseCase(audioPlayer)

    private val savedTrackUi = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        intent.getParcelableExtra(SAVE_TRACK_FOR_AUDIO_PLAYER_KEY, TrackUi::class.java)
    } else {
        intent.getParcelableExtra(SAVE_TRACK_FOR_AUDIO_PLAYER_KEY)
    }
    private val savedTrack = savedTrackUi?.let {
        Track(
            trackId = it.trackId,
            trackName = it.trackName,
            artistName = it.artistName,
            trackTimeMillis = it.trackTimeMillis,
            trackTime = it.trackTime,
            artworkUrl100 = it.artworkUrl100,
            artworkUrl512 = it.artworkUrl512,
            collectionName = it.collectionName,
            releaseDate = it.releaseDate,
            primaryGenreName = it.primaryGenreName,
            country = it.country,
            previewUrl = it.previewUrl
        )
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AudioPlayerViewModel(
            savedTrack = savedTrack,
            preparePlayer = preparePlayer,
            pausePlayer = pausePlayer,
            startPlayer = startPlayer,
            releasePlayer = releasePlayer,
            getPlayerCurrentPosition = getPlayerCurrentPosition,
            getPlayerState = getPlayerState,
        ) as T
    }

}