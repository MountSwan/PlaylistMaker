package com.practicum.playlistmaker.creator

import android.content.Context
import com.practicum.playlistmaker.player.data.AudioPlayerImpl
import com.practicum.playlistmaker.player.domain.AudioPlayer
import com.practicum.playlistmaker.search.data.network.NetworkClientImpl
import com.practicum.playlistmaker.search.data.sharedprefs.SearchHistoryImpl
import com.practicum.playlistmaker.search.data.network.TracksRepositoryImpl
import com.practicum.playlistmaker.search.data.sharedprefs.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.search.domain.SearchHistoryRepository
import com.practicum.playlistmaker.search.domain.TracksRepository
import com.practicum.playlistmaker.settings.data.ExternalNavigatorImpl
import com.practicum.playlistmaker.settings.data.sharedprefs.ThemeSwitcherStateImpl
import com.practicum.playlistmaker.settings.domain.ExternalNavigator
import com.practicum.playlistmaker.settings.domain.ThemeSwitcherState

object Creator {

    fun provideAudioPlayer(): AudioPlayer {
        return AudioPlayerImpl()
    }

    fun provideTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(NetworkClientImpl())
    }

    fun provideSearchHistoryRepository(context: Context): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(SearchHistoryImpl(context))
    }

    fun provideThemeSwitcherState(context: Context): ThemeSwitcherState {
        return ThemeSwitcherStateImpl(context)
    }

    fun provideExternalNavigator(context: Context): ExternalNavigator {
        return ExternalNavigatorImpl(context)
    }

}