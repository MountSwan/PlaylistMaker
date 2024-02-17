package com.practicum.playlistmaker.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.AddInHistoryUseCase
import com.practicum.playlistmaker.search.domain.ClearHistoryUseCase
import com.practicum.playlistmaker.search.domain.GetTracksFromSharedPrefsUseCase
import com.practicum.playlistmaker.search.domain.SearchTracksUseCase
import com.practicum.playlistmaker.search.domain.models.NetworkRequestState
import com.practicum.playlistmaker.search.domain.models.SearchState
import com.practicum.playlistmaker.search.domain.models.ShowMessage
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.launch

class TracksSearchViewModel(
    private val searchTracks: SearchTracksUseCase,
    private val getTracksFromSharedPrefs: GetTracksFromSharedPrefsUseCase,
    private val addInHistory: AddInHistoryUseCase,
    private val clearHistory: ClearHistoryUseCase,
) : ViewModel() {

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
        getTracksFromSharedPrefs.execute(tracksInHistory)
        searchState.tracksInHistorySize = tracksInHistory.size
        tracksInHistoryLiveData.value = tracksInHistory
    }

    fun tracksClear() {
        tracks.clear()
        tracksLiveData.value = tracks
    }

    fun searchTracks(
        searchRequest: String,
        nothingFoundMessage: String,
        somethingWentWrongMessage: String,
    ) {
        searchState.refreshButtonIsVisible = false
        searchState.recyclerViewIsVisible = false
        searchState.progressBarIsVisible = true
        searchStateLiveData.value = searchState

        viewModelScope.launch {
            searchTracks.execute(
                searchRequest = searchRequest,
                searchState = searchState,
                tracks = tracks
            )
                .collect {
                    executeRequestResult(
                        networkRequestState = it,
                        nothingFoundMessage = nothingFoundMessage,
                        somethingWentWrongMessage = somethingWentWrongMessage
                    )
                }
        }

    }

    fun doOnTextChange(textSearch: CharSequence?) {
        searchState.clearButtonVisibility = !textSearch.isNullOrEmpty()
        searchState.tracksInHistorySize = tracksInHistory.size
        searchStateLiveData.value = searchState
    }

    fun clearHistory() {
        clearHistory.execute(tracksInHistory)
    }

    fun addInHistory(track: Track) {
        addInHistory.execute(track, tracksInHistory)
    }

    private fun showMessage(text: String, image: Int) {
        showMessage.text = text
        showMessage.image = image
        showMessageLiveData.postValue(showMessage)
    }

    private fun executeRequestResult(
        networkRequestState: NetworkRequestState,
        nothingFoundMessage: String,
        somethingWentWrongMessage: String
    ) {
        searchState.progressBarIsVisible = false
        searchStateLiveData.postValue(searchState)
        when (networkRequestState) {
            is NetworkRequestState.OnResponse.ExecutedRequest -> {
                if (searchState.responseResultsIsNotEmpty) {
                    searchState.responseResultsIsNotEmpty = false
                    tracksLiveData.postValue(tracks)
                    searchState.recyclerViewIsVisible = true
                    searchState.adapterNotifyDataSetChanged = true
                    searchStateLiveData.postValue(searchState)
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
                searchStateLiveData.postValue(searchState)
            }

            is NetworkRequestState.OnFailure -> {
                searchState.progressBarIsVisible = false
                searchStateLiveData.postValue(searchState)
                showMessage(somethingWentWrongMessage, R.drawable.disconnect)
                searchState.refreshButtonIsVisible = true
                searchStateLiveData.postValue(searchState)
            }

            is NetworkRequestState.Default -> Unit
        }
    }

}