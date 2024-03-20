package com.practicum.playlistmaker.library.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.library.domain.models.Playlist
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val playlistInteractor: PlaylistInteractor) :
    ViewModel() {

    private val playlistsLiveData =
        MutableLiveData<ArrayList<Playlist>>()

    fun observePlaylists(): LiveData<ArrayList<Playlist>> = playlistsLiveData

    fun getPlaylistsFromDatabase() {
        viewModelScope.launch {
            playlistInteractor.getPlaylists().collect {
                playlistsLiveData.postValue(it)
            }
        }
    }

}