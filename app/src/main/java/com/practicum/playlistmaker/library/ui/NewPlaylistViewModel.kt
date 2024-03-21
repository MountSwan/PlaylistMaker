package com.practicum.playlistmaker.library.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.library.domain.models.Playlist
import kotlinx.coroutines.launch

open class NewPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val playlistId: Int,
) : ViewModel() {

    private val playlistInfoLiveData =
        MutableLiveData<Playlist>()

    fun observePlaylistInfo(): LiveData<Playlist> = playlistInfoLiveData

    private val insertingPlaylistIsCompleteLiveData =
        MutableLiveData<Boolean>()

    fun observeInsertingPlaylistIsComplete(): LiveData<Boolean> =
        insertingPlaylistIsCompleteLiveData

    fun insertPlaylistInDatabase(nameOfPlaylist: String, descriptionOfPlaylist: String) {
        if (playlistId == 0) {
            viewModelScope.launch {
                playlistInteractor.getPlaylists().collect { playlistsInDataBase ->
                    playlistInteractor.insertPlaylist(
                        Playlist(
                            playlistId = playlistsInDataBase.size + 1,
                            playlistName = nameOfPlaylist,
                            playlistDescription = descriptionOfPlaylist,
                            listOfTracksId = "",
                            numberOfTracksInPlaylist = 0,
                        )
                    )
                }
                insertingPlaylistIsCompleteLiveData.postValue(true)
            }
        } else {
            viewModelScope.launch {
                playlistInteractor.getPlaylistInfo(playlistId).collect { playlist ->
                    playlist.playlistName = nameOfPlaylist
                    playlist.playlistDescription = descriptionOfPlaylist
                    playlistInteractor.insertPlaylist(playlist)
                }
                insertingPlaylistIsCompleteLiveData.postValue(true)
            }
        }
    }

    fun getPlaylistInfoFromDatabase() {
        if (playlistId > 0) {
            viewModelScope.launch {
                playlistInteractor.getPlaylistInfo(playlistId).collect { playlist ->
                    playlistInfoLiveData.postValue(playlist)
                }
            }
        }
    }

}