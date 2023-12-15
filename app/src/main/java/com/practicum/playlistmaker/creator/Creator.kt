package com.practicum.playlistmaker.creator

import android.content.Context
import android.content.Intent
import com.practicum.playlistmaker.player.data.AudioPlayerImpl
import com.practicum.playlistmaker.player.data.GetSavedTrackImpl
import com.practicum.playlistmaker.player.domain.AudioPlayer
import com.practicum.playlistmaker.player.domain.GetSavedTrack
import com.practicum.playlistmaker.search.data.StartAudioPlayerImpl
import com.practicum.playlistmaker.search.data.network.NetworkClientImpl
import com.practicum.playlistmaker.search.data.sharedprefs.SearchHistoryImpl
import com.practicum.playlistmaker.search.domain.NetworkClient
import com.practicum.playlistmaker.search.domain.SearchHistory
import com.practicum.playlistmaker.search.domain.StartAudioPlayer
import com.practicum.playlistmaker.settings.data.ExternalNavigatorImpl
import com.practicum.playlistmaker.settings.data.sharedprefs.ThemeSwitcherStateImpl
import com.practicum.playlistmaker.settings.domain.ExternalNavigator
import com.practicum.playlistmaker.settings.domain.ThemeSwitcherState

object Creator {

    fun provideAudioPlayer(): AudioPlayer {
        return AudioPlayerImpl()
    }

    fun provideNetworkClient(): NetworkClient {
        return NetworkClientImpl()
    }

    fun provideSearchHistory(context: Context): SearchHistory {
        return SearchHistoryImpl(context)
    }

    fun provideThemeSwitcherState(context: Context): ThemeSwitcherState {
        return ThemeSwitcherStateImpl(context)
    }

    fun provideExternalNavigator(context: Context): ExternalNavigator {
        return ExternalNavigatorImpl(context)
    }

    fun provideStartAudioPlayer(context: Context, audioPlayerIntent: Intent): StartAudioPlayer {
        return StartAudioPlayerImpl(context, audioPlayerIntent)
    }

    fun provideGetSavedTrack(intent: Intent): GetSavedTrack {
        return GetSavedTrackImpl(intent)
    }

}