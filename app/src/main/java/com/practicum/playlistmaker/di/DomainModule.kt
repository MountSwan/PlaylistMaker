package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.player.domain.MediaPlayerInteractor
import com.practicum.playlistmaker.player.domain.MediaPlayerInteractorImpl
import com.practicum.playlistmaker.search.domain.AddInHistoryUseCase
import com.practicum.playlistmaker.search.domain.ClearHistoryUseCase
import com.practicum.playlistmaker.search.domain.GetTracksFromSharedPrefsUseCase
import com.practicum.playlistmaker.search.domain.SearchTracksUseCase
import com.practicum.playlistmaker.search.domain.usecases.AddInHistoryUseCaseImpl
import com.practicum.playlistmaker.search.domain.usecases.ClearHistoryUseCaseImpl
import com.practicum.playlistmaker.search.domain.usecases.GetTracksFromSharedPrefsUseCaseImpl
import com.practicum.playlistmaker.search.domain.usecases.SearchTracksUseCaseImpl
import com.practicum.playlistmaker.settings.domain.EditSharedPrefsUseCase
import com.practicum.playlistmaker.settings.domain.GetSupportUseCase
import com.practicum.playlistmaker.settings.domain.ReadUserAgreementUseCase
import com.practicum.playlistmaker.settings.domain.ShareAppUseCase
import com.practicum.playlistmaker.settings.domain.usecases.EditSharedPrefsUseCaseImpl
import com.practicum.playlistmaker.settings.domain.usecases.GetSupportUseCaseImpl
import com.practicum.playlistmaker.settings.domain.usecases.ReadUserAgreementUseCaseImpl
import com.practicum.playlistmaker.settings.domain.usecases.ShareAppUseCaseImpl
import org.koin.dsl.module

val domainModule = module {

    single<GetTracksFromSharedPrefsUseCase> {
        GetTracksFromSharedPrefsUseCaseImpl(get())
    }

    single<AddInHistoryUseCase> {
        AddInHistoryUseCaseImpl(get())
    }

    single<ClearHistoryUseCase> {
        ClearHistoryUseCaseImpl(get())
    }

    single<SearchTracksUseCase> {
        SearchTracksUseCaseImpl(get())
    }

    factory<MediaPlayerInteractor> {
        MediaPlayerInteractorImpl(get())
    }

    single<EditSharedPrefsUseCase> {
        EditSharedPrefsUseCaseImpl(get())
    }

    single<ShareAppUseCase> {
        ShareAppUseCaseImpl(get())
    }

    single<GetSupportUseCase> {
        GetSupportUseCaseImpl(get())
    }

    single<ReadUserAgreementUseCase> {
        ReadUserAgreementUseCaseImpl(get())
    }

}