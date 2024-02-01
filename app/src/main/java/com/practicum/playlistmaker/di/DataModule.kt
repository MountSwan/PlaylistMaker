package com.practicum.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import com.google.gson.Gson
import com.practicum.playlistmaker.player.data.AudioPlayerImpl
import com.practicum.playlistmaker.player.domain.AudioPlayer
import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.SearchHistory
import com.practicum.playlistmaker.search.data.network.ITunesApi
import com.practicum.playlistmaker.search.data.network.NetworkClientImpl
import com.practicum.playlistmaker.search.data.repository.TracksRepositoryImpl
import com.practicum.playlistmaker.search.data.sharedprefs.SearchHistoryImpl
import com.practicum.playlistmaker.search.data.sharedprefs.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.search.domain.SearchHistoryRepository
import com.practicum.playlistmaker.search.domain.TracksRepository
import com.practicum.playlistmaker.settings.data.ExternalNavigatorImpl
import com.practicum.playlistmaker.settings.data.sharedprefs.ThemeSwitcherStateImpl
import com.practicum.playlistmaker.settings.domain.ExternalNavigator
import com.practicum.playlistmaker.settings.domain.ThemeSwitcherState
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single<ITunesApi> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApi::class.java)
    }

    single<SharedPreferences> {
        androidContext()
            .getSharedPreferences("practicum_example_preferences", Context.MODE_PRIVATE)
    }

    factory { Gson() }

    single<NetworkClient> {
        NetworkClientImpl(get(), androidContext())
    }

    single<SearchHistory> {
        SearchHistoryImpl(get(), get())
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get())
    }

    single<TracksRepository> {
        TracksRepositoryImpl(get())
    }

    single<AudioPlayer> {
        AudioPlayerImpl(get())
    }

    single { MediaPlayer() }

    single<ThemeSwitcherState> {
        ThemeSwitcherStateImpl(get())
    }

    single<ExternalNavigator> {
        ExternalNavigatorImpl(androidContext())
    }

}