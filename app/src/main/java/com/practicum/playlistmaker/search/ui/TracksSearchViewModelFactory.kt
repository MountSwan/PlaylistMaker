package com.practicum.playlistmaker.search.ui

import android.content.Context
import android.content.Intent
import android.os.Handler
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.player.ui.AudioPlayerActivity
import com.practicum.playlistmaker.search.domain.usecases.GetNetworkRequestStateUseCase
import com.practicum.playlistmaker.search.domain.usecases.StartAudioPlayerUseCase

class TracksSearchViewModelFactory(context: Context, private val mainThreadHandler: Handler) :
    ViewModelProvider.Factory {

    private val nothingFoundMessage = context.getString(R.string.nothing_found)
    private val somethingWentWrongMessage = context.getString(R.string.something_went_wrong)
    private val networkClient = Creator.provideNetworkClient()
    private val searchHistory = Creator.provideSearchHistory(context)
    private val getNetworkRequestState = GetNetworkRequestStateUseCase(networkClient)
    private val audioPlayerIntent = Intent(context, AudioPlayerActivity::class.java)
    private val startAudioPlayer = Creator.provideStartAudioPlayer(context, audioPlayerIntent)
    private val startAudioPlayerUseCase = StartAudioPlayerUseCase(startAudioPlayer)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TracksSearchViewModel(
            searchHistory = searchHistory,
            nothingFoundMessage = nothingFoundMessage,
            somethingWentWrongMessage = somethingWentWrongMessage,
            mainThreadHandler = mainThreadHandler,
            networkClient = networkClient,
            getNetworkRequestState = getNetworkRequestState,
            startAudioPlayerUseCase = startAudioPlayerUseCase,
        ) as T
    }

}