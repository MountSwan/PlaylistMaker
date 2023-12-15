package com.practicum.playlistmaker.player.ui

import android.content.Intent
import android.os.Handler
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.player.domain.usecases.GetMediaPlayerCurrentPositionUseCase
import com.practicum.playlistmaker.player.domain.usecases.GetMediaPlayerStateUseCase
import com.practicum.playlistmaker.player.domain.usecases.GetSavedTrackUseCase
import com.practicum.playlistmaker.player.domain.usecases.PauseMediaPlayerUseCase
import com.practicum.playlistmaker.player.domain.usecases.PrepareMediaPlayerUseCase
import com.practicum.playlistmaker.player.domain.usecases.ReleaseMediaPlayerUseCase
import com.practicum.playlistmaker.player.domain.usecases.StartMediaPlayerUseCase

class AudioPlayerViewModelFactory(private val mainThreadHandler: Handler, intent: Intent) :
    ViewModelProvider.Factory {

    private val audioPlayer = Creator.provideAudioPlayer()
    private val preparePlayer = PrepareMediaPlayerUseCase(audioPlayer)
    private val pausePlayer = PauseMediaPlayerUseCase(audioPlayer)
    private val startPlayer = StartMediaPlayerUseCase(audioPlayer)
    private val releasePlayer = ReleaseMediaPlayerUseCase(audioPlayer)
    private val getPlayerCurrentPosition = GetMediaPlayerCurrentPositionUseCase(audioPlayer)
    private val getPlayerState = GetMediaPlayerStateUseCase(audioPlayer)
    private val getSavedTrack = Creator.provideGetSavedTrack(intent)
    private val getSavedTrackUseCase = GetSavedTrackUseCase(getSavedTrack)
    private val savedTrack = getSavedTrackUseCase.execute()

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AudioPlayerViewModel(
            mainThreadHandler = mainThreadHandler,
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