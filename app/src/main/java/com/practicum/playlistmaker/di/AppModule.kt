package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.library.ui.FavoriteTracksViewModel
import com.practicum.playlistmaker.library.ui.PlaylistsViewModel
import com.practicum.playlistmaker.main.ui.MainActivityViewModel
import com.practicum.playlistmaker.player.ui.AudioPlayerViewModel
import com.practicum.playlistmaker.search.domain.models.Track
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
            favoriteTrackInteractor = get(),
        )
    }

    viewModel {
        MainActivityViewModel()
    }

    viewModel {
        FavoriteTracksViewModel(favoriteTrackInteractor = get())
    }

    viewModel {
        PlaylistsViewModel()
    }

    viewModel {(savedTrack: Track) ->
        AudioPlayerViewModel(
            savedTrack = savedTrack,
            mediaPlayerInteractor = get(),
            favoriteTrackInteractor = get(),
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