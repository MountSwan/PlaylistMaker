package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.main.ui.MainActivityViewModel
import com.practicum.playlistmaker.player.ui.AudioPlayerViewModel
import com.practicum.playlistmaker.search.ui.TracksSearchViewModel
import com.practicum.playlistmaker.settings.ui.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    viewModel {
        TracksSearchViewModel(
            searchTracks = get(),
            getTracksFromSharedPrefs = get(),
            addInHistory = get(),
            clearHistory = get(),
        )
    }

    viewModel {
        MainActivityViewModel()
    }

    viewModel {
        AudioPlayerViewModel(
            preparePlayer = get(),
            pausePlayer = get(),
            startPlayer = get(),
            releasePlayer = get(),
            getPlayerCurrentPosition = get(),
            getPlayerState = get(),
        )
    }

    viewModel {
        SettingsViewModel(
            editSharedPrefs = get(),
            shareApp = get(),
            getSupport = get(),
            readUserAgreement = get()
        )
    }

}