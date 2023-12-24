package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.player.domain.usecases.GetMediaPlayerCurrentPositionUseCase
import com.practicum.playlistmaker.player.domain.usecases.GetMediaPlayerStateUseCase
import com.practicum.playlistmaker.player.domain.usecases.PauseMediaPlayerUseCase
import com.practicum.playlistmaker.player.domain.usecases.PrepareMediaPlayerUseCase
import com.practicum.playlistmaker.player.domain.usecases.ReleaseMediaPlayerUseCase
import com.practicum.playlistmaker.player.domain.usecases.StartMediaPlayerUseCase
import com.practicum.playlistmaker.search.domain.usecases.AddInHistoryUseCase
import com.practicum.playlistmaker.search.domain.usecases.ClearHistoryUseCase
import com.practicum.playlistmaker.search.domain.usecases.GetTracksFromSharedPrefsUseCase
import com.practicum.playlistmaker.settings.domain.usecases.EditSharedPrefsUseCase
import com.practicum.playlistmaker.settings.domain.usecases.GetSupportUseCase
import com.practicum.playlistmaker.settings.domain.usecases.ReadUserAgreementUseCase
import com.practicum.playlistmaker.settings.domain.usecases.ShareAppUseCase
import org.koin.dsl.module

val domainModule = module {

    single {
        GetTracksFromSharedPrefsUseCase(get())
    }

    single {
        AddInHistoryUseCase(get())
    }

    single {
        ClearHistoryUseCase(get())
    }

    single {
        PrepareMediaPlayerUseCase(get())
    }

    single {
        PauseMediaPlayerUseCase(get())
    }

    single {
        StartMediaPlayerUseCase(get())
    }

    single {
        ReleaseMediaPlayerUseCase(get())
    }

    single {
        GetMediaPlayerCurrentPositionUseCase(get())
    }

    single {
        GetMediaPlayerStateUseCase(get())
    }

    single {
        EditSharedPrefsUseCase(get())
    }

    single {
        ShareAppUseCase(get())
    }

    single {
        GetSupportUseCase(get())
    }

    single {
        ReadUserAgreementUseCase(get())
    }

}