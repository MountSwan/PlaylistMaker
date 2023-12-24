package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.player.domain.GetMediaPlayerCurrentPosition
import com.practicum.playlistmaker.player.domain.GetMediaPlayerState
import com.practicum.playlistmaker.player.domain.PauseMediaPlayer
import com.practicum.playlistmaker.player.domain.PrepareMediaPlayer
import com.practicum.playlistmaker.player.domain.ReleaseMediaPlayer
import com.practicum.playlistmaker.player.domain.StartMediaPlayer
import com.practicum.playlistmaker.player.domain.usecases.GetMediaPlayerCurrentPositionUseCase
import com.practicum.playlistmaker.player.domain.usecases.GetMediaPlayerStateUseCase
import com.practicum.playlistmaker.player.domain.usecases.PauseMediaPlayerUseCase
import com.practicum.playlistmaker.player.domain.usecases.PrepareMediaPlayerUseCase
import com.practicum.playlistmaker.player.domain.usecases.ReleaseMediaPlayerUseCase
import com.practicum.playlistmaker.player.domain.usecases.StartMediaPlayerUseCase
import com.practicum.playlistmaker.search.domain.AddInHistory
import com.practicum.playlistmaker.search.domain.ClearHistory
import com.practicum.playlistmaker.search.domain.GetTracksFromSharedPrefs
import com.practicum.playlistmaker.search.domain.SearchTracks
import com.practicum.playlistmaker.search.domain.usecases.AddInHistoryUseCase
import com.practicum.playlistmaker.search.domain.usecases.ClearHistoryUseCase
import com.practicum.playlistmaker.search.domain.usecases.GetTracksFromSharedPrefsUseCase
import com.practicum.playlistmaker.search.domain.usecases.SearchTracksUseCase
import com.practicum.playlistmaker.settings.domain.EditSharedPrefs
import com.practicum.playlistmaker.settings.domain.GetSupport
import com.practicum.playlistmaker.settings.domain.ReadUserAgreement
import com.practicum.playlistmaker.settings.domain.ShareApp
import com.practicum.playlistmaker.settings.domain.usecases.EditSharedPrefsUseCase
import com.practicum.playlistmaker.settings.domain.usecases.GetSupportUseCase
import com.practicum.playlistmaker.settings.domain.usecases.ReadUserAgreementUseCase
import com.practicum.playlistmaker.settings.domain.usecases.ShareAppUseCase
import org.koin.dsl.module

val domainModule = module {

    single<GetTracksFromSharedPrefs> {
        GetTracksFromSharedPrefsUseCase(get())
    }

    single<AddInHistory> {
        AddInHistoryUseCase(get())
    }

    single<ClearHistory> {
        ClearHistoryUseCase(get())
    }

    single<SearchTracks> {
        SearchTracksUseCase(get())
    }

    single<PrepareMediaPlayer> {
        PrepareMediaPlayerUseCase(get())
    }

    single<PauseMediaPlayer> {
        PauseMediaPlayerUseCase(get())
    }

    single<StartMediaPlayer> {
        StartMediaPlayerUseCase(get())
    }

    single<ReleaseMediaPlayer> {
        ReleaseMediaPlayerUseCase(get())
    }

    single<GetMediaPlayerCurrentPosition> {
        GetMediaPlayerCurrentPositionUseCase(get())
    }

    single<GetMediaPlayerState> {
        GetMediaPlayerStateUseCase(get())
    }

    single<EditSharedPrefs> {
        EditSharedPrefsUseCase(get())
    }

    single<ShareApp> {
        ShareAppUseCase(get())
    }

    single<GetSupport> {
        GetSupportUseCase(get())
    }

    single<ReadUserAgreement> {
        ReadUserAgreementUseCase(get())
    }

}