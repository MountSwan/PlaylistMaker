package com.practicum.playlistmaker.search.ui

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.NetworkClient
import com.practicum.playlistmaker.search.domain.SearchHistory
import com.practicum.playlistmaker.search.domain.models.NetworkRequestState
import com.practicum.playlistmaker.search.domain.models.SearchState
import com.practicum.playlistmaker.search.domain.models.ShowMessage
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.domain.usecases.GetNetworkRequestStateUseCase
import com.practicum.playlistmaker.search.domain.usecases.StartAudioPlayerUseCase

class TracksSearchViewModel(
    private val searchHistory: SearchHistory,
    private val nothingFoundMessage: String,
    private val somethingWentWrongMessage: String,
    private val mainThreadHandler: Handler,
    private val networkClient: NetworkClient,
    private val getNetworkRequestState: GetNetworkRequestStateUseCase,
    private val startAudioPlayerUseCase: StartAudioPlayerUseCase,
) : ViewModel() {

    companion object {
        const val REFRESH_CHECKING_MILLIS = 500L
    }


    private val listenerNetworkClient = checkRequestCompletion()

    private val showMessage = ShowMessage(
        text = "",
        image = R.drawable.no_found
    )

    private val searchState = SearchState(
        refreshButtonIsVisible = false,
        recyclerViewIsVisible = false,
        progressBarIsVisible = true,
        adapterNotifyDataSetChanged = false,
        clearButtonVisibility = false,
        tracksInHistorySize = 0,
        requestIsComplete = false,
        responseResultsIsNotEmpty = false,
    )

    private val tracks = ArrayList<Track>()
    private val tracksInHistory = ArrayList<Track>()

    private val tracksLiveData =
        MutableLiveData<ArrayList<Track>>()

    fun observeTracks(): LiveData<ArrayList<Track>> = tracksLiveData

    private val tracksInHistoryLiveData =
        MutableLiveData<ArrayList<Track>>()

    fun observeTracksInHistory(): LiveData<ArrayList<Track>> = tracksInHistoryLiveData

    private val searchStateLiveData =
        MutableLiveData<SearchState>()

    fun observeSearchState(): LiveData<SearchState> = searchStateLiveData

    private val showMessageLiveData =
        MutableLiveData<ShowMessage>()

    fun observeShowMessage(): LiveData<ShowMessage> = showMessageLiveData

    fun getTracksInHistoryFromSharedPrefs() {
        searchHistory.getTracksFromSharedPrefs(tracksInHistory)
        searchState.tracksInHistorySize = tracksInHistory.size
        tracksInHistoryLiveData.value = tracksInHistory
    }

    fun tracksClear() {
        tracks.clear()
        tracksLiveData.value = tracks
    }

    fun searchTracks(searchRequest: String) {
        searchState.refreshButtonIsVisible = false
        searchState.recyclerViewIsVisible = false
        searchState.progressBarIsVisible = true
        searchStateLiveData.value = searchState

        networkClient.doRequest(
            searchRequest = searchRequest,
            searchState = searchState,
            tracks = tracks
        )

        mainThreadHandler.post(listenerNetworkClient)

    }

    fun doOnTextChange(textSearch: CharSequence?) {
        searchState.clearButtonVisibility = !textSearch.isNullOrEmpty()
        searchState.tracksInHistorySize = tracksInHistory.size
        searchStateLiveData.value = searchState
    }

    fun clearHistory() {
        searchHistory.clearHistory(tracksInHistory)
    }

    fun addInHistory(track: Track) {
        searchHistory.addInHistory(track, tracksInHistory)
    }

    fun startAudioPlayer(track: Track) {
        startAudioPlayerUseCase.execute(track)

    }

    private fun showMessage(text: String, image: Int) {
        showMessage.text = text
        showMessage.image = image
        showMessageLiveData.value = showMessage
    }

    private fun checkRequestCompletion(): Runnable {
        return object : Runnable {
            override fun run() {
                if (searchState.requestIsComplete) {
                    mainThreadHandler.removeCallbacks(listenerNetworkClient)
                    searchState.requestIsComplete = false
                    executeRequestResult()
                } else {
                    mainThreadHandler.postDelayed(this, REFRESH_CHECKING_MILLIS)
                }
            }
        }
    }

    private fun executeRequestResult() {
        searchState.progressBarIsVisible = false
        searchStateLiveData.value = searchState
        when (getNetworkRequestState.execute()) {
            is NetworkRequestState.OnResponse.ExecutedRequest -> {
                if (searchState.responseResultsIsNotEmpty) {
                    searchState.responseResultsIsNotEmpty = false
                    tracksLiveData.value = tracks
                    searchState.recyclerViewIsVisible = true
                    searchState.adapterNotifyDataSetChanged = true
                    searchStateLiveData.value = searchState
                    searchState.adapterNotifyDataSetChanged = false
                }
                if (tracks.isEmpty()) {
                    showMessage(nothingFoundMessage, R.drawable.no_found)
                } else {
                    showMessage("", R.drawable.no_found)
                }
            }

            is NetworkRequestState.OnResponse.IsNotExecutedRequest -> {
                showMessage(somethingWentWrongMessage, R.drawable.disconnect)
                searchState.refreshButtonIsVisible = true
                searchStateLiveData.value = searchState
            }

            is NetworkRequestState.OnFailure -> {
                searchState.progressBarIsVisible = false
                searchStateLiveData.value = searchState
                showMessage(somethingWentWrongMessage, R.drawable.disconnect)
                searchState.refreshButtonIsVisible = true
                searchStateLiveData.value = searchState
            }

            is NetworkRequestState.Default -> Unit
        }
    }

}