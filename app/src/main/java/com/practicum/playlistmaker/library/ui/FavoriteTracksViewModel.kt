package com.practicum.playlistmaker.library.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.domain.db.FavoriteTrackInteractor
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(private val favoriteTrackInteractor: FavoriteTrackInteractor) :
    ViewModel() {

    private val favoriteTrackStateLiveData =
        MutableLiveData<FavoriteTracksState>()

    fun observeFavoriteTrackState(): LiveData<FavoriteTracksState> = favoriteTrackStateLiveData

    init {
        getFavoriteTracks()
    }

    fun getFavoriteTracks() {
        viewModelScope.launch {
            favoriteTrackInteractor.getFavoriteTracks().collect {
                if (it.isEmpty()) {
                    favoriteTrackStateLiveData.postValue(FavoriteTracksState.Empty)
                } else {
                    favoriteTrackStateLiveData.postValue(FavoriteTracksState.Content(tracks = it))
                }
            }
        }
    }

    suspend fun checkIsFavorite(trackID: Long?): Boolean {
        return favoriteTrackInteractor.checkIsFavorite(trackID)
    }

}