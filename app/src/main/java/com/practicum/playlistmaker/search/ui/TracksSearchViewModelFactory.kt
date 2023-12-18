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

class TracksSearchViewModelFactory(context: Context, private val mainThreadHandler: Handler) :
    ViewModelProvider.Factory {

    private val nothingFoundMessage = context.getString(R.string.nothing_found)
    private val somethingWentWrongMessage = context.getString(R.string.something_went_wrong)
    private val tracksRepository = Creator.provideTracksRepository()
    private val searchHistory = Creator.provideSearchHistoryRepository(context)
    private val getNetworkRequestState = GetNetworkRequestStateUseCase(tracksRepository)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TracksSearchViewModel(
            searchHistory = searchHistory,
            nothingFoundMessage = nothingFoundMessage,
            somethingWentWrongMessage = somethingWentWrongMessage,
            mainThreadHandler = mainThreadHandler,
            tracksRepository = tracksRepository,
            getNetworkRequestState = getNetworkRequestState,
        ) as T
    }

}