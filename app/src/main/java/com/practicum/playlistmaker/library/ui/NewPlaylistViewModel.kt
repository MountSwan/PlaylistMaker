package com.practicum.playlistmaker.library.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.library.domain.models.Playlist
import kotlinx.coroutines.launch

class NewPlaylistViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    private val insertingPlaylistIsCompleteLiveData =
        MutableLiveData<Boolean>()

    fun observeInsertingPlaylistIsComplete(): LiveData<Boolean> =
        insertingPlaylistIsCompleteLiveData

    fun insertPlaylistInDatabase(nameOfNewPlaylist: String, descriptionOfNewPlaylist: String) {
        insertingPlaylistIsCompleteLiveData.value = false
        viewModelScope.launch {
            playlistInteractor.getPlaylists().collect { playlistsInDataBase ->
                playlistInteractor.insertPlaylist(
                    Playlist(
                        playlistId = playlistsInDataBase.size + 1,
                        playlistName = nameOfNewPlaylist,
                        playlistDescription = descriptionOfNewPlaylist,
                        listOfTracksId = "",
                        numberOfTracksInPlaylist = 0,
                    )
                )
            }
            playlistInteractor.getPlaylists().collect {
                Log.e("AAA", "${it.size}")
            }
            insertingPlaylistIsCompleteLiveData.value = true
        }

    }

}