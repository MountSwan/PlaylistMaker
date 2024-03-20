package com.practicum.playlistmaker.library.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.library.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.settings.domain.ShareAppUseCase
import kotlinx.coroutines.launch

class PlaylistInfoViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val playlistId: Int,
    private val shareApp: ShareAppUseCase,
    private val context: Context,
) : ViewModel() {

    private var closeFragment = true

    private val playlistInfoLiveData =
        MutableLiveData<Playlist>()

    fun observePlaylistInfo(): LiveData<Playlist> = playlistInfoLiveData

    private val tracksInPlaylistLiveData =
        MutableLiveData<ArrayList<Track>>()

    fun observeTracksInPlaylist(): LiveData<ArrayList<Track>> = tracksInPlaylistLiveData

    private val sharePossibilityLiveData =
        MutableLiveData<Boolean>()

    fun observeSharePossibility(): LiveData<Boolean> = sharePossibilityLiveData

    private val closeFragmentLiveData =
        MutableLiveData<Boolean>()

    fun observeCloseFragment(): LiveData<Boolean> = closeFragmentLiveData

    fun getPlaylistInfoFromDatabase() {
        viewModelScope.launch {
            playlistInteractor.getPlaylistInfo(playlistId).collect { playlist ->
                playlistInfoLiveData.postValue(playlist)
                val tracksInPlaylist = ArrayList<Track>()
                if (playlist.listOfTracksId.isNotEmpty()) {
                    val listOfTracksIdInPlaylist =
                        playlistInteractor.getListOfTracksId(playlist.listOfTracksId)
                    listOfTracksIdInPlaylist.forEach { trackId ->
                        playlistInteractor.getTrack(trackId).collect { track ->
                            tracksInPlaylist.add(track)
                        }
                    }
                }
                tracksInPlaylistLiveData.postValue(tracksInPlaylist)
            }
        }
    }

    fun deleteTrackFromPlaylist(trackId: Long?) {
        val listOfTracksId = ArrayList<Long>()
        viewModelScope.launch {
            playlistInteractor.getPlaylistInfo(playlistId).collect { playlist ->
                listOfTracksId.addAll(playlistInteractor.getListOfTracksId(playlist.listOfTracksId))
                listOfTracksId.remove(trackId)
                if (listOfTracksId.size == 0) {
                    playlist.listOfTracksId = ""
                } else {
                    playlist.listOfTracksId =
                        playlistInteractor.putListOfTracksIdInJson(listOfTracksId)

                }
                playlist.numberOfTracksInPlaylist = listOfTracksId.size
                playlistInteractor.insertPlaylist(playlist)
            }

            listOfTracksId.clear()
            if (trackId != null) {
                listOfTracksId.add(trackId)
            }
            closeFragment = false
            deleteUnusedTracks(listOfTracksId)

            getPlaylistInfoFromDatabase()
        }
    }

    fun checkSharePossibility() {
        viewModelScope.launch {
            playlistInteractor.getPlaylistInfo(playlistId).collect {
                sharePossibilityLiveData.postValue(it.listOfTracksId.isNotEmpty())
            }
        }
    }

    fun sharePlaylist() {
        viewModelScope.launch {
            var shareMessage = ""
            playlistInteractor.getPlaylistInfo(playlistId).collect { playlist ->
                shareMessage = "${context.getString(R.string.playlist)}: ${playlist.playlistName}\n"
                if (playlist.playlistDescription.isNotEmpty()) {
                    shareMessage += "${context.getString(R.string.playlist_description)}: ${playlist.playlistDescription}\n"
                }
                val listOfTracksIdInPlaylist =
                    playlistInteractor.getListOfTracksId(playlist.listOfTracksId)
                shareMessage += "${listOfTracksIdInPlaylist.size} ${
                    defineEnd(
                        listOfTracksIdInPlaylist.size
                    )
                }\n"
                var trackNumber = 1
                listOfTracksIdInPlaylist.forEach { trackId ->
                    playlistInteractor.getTrack(trackId).collect { track ->
                        shareMessage += "$trackNumber. ${track.artistName} - ${track.trackName} (${track.trackTime})\n"

                    }
                    trackNumber += 1
                }
            }
            shareApp.execute(shareMessage)
        }
    }

    private fun defineEnd(number: Int): String {
        return if (number < 10) {
            when (number) {
                1 -> context.getString(R.string.track)
                2, 3, 4 -> context.getString(R.string.of_track)
                else -> context.getString(R.string.of_tracks)
            }
        } else if (number.toString()[number.toString().length - 2].digitToInt() == 1) {
            context.getString(R.string.of_tracks)
        } else {
            when (number.toString().last().digitToInt()) {
                1 -> context.getString(R.string.track)
                2, 3, 4 -> context.getString(R.string.of_track)
                else -> context.getString(R.string.of_tracks)
            }
        }
    }

    fun deletePlaylist() {
        val listOfTracksId = ArrayList<Long>()
        viewModelScope.launch {
            playlistInteractor.getPlaylistInfo(playlistId).collect { playlist ->
                if (playlist.listOfTracksId.isNotEmpty()) {
                    listOfTracksId.addAll(playlistInteractor.getListOfTracksId(playlist.listOfTracksId))
                }
            }
            playlistInteractor.deletePlaylist(playlistId)
            playlistInteractor.getPlaylists().collect { allPlaylists ->
                if (allPlaylists.size > playlistId - 1) {
                    var playlistNumber = playlistId
                    while (playlistNumber < allPlaylists.size + 1) {
                        playlistInteractor.getPlaylistInfo(playlistNumber + 1).collect { playlist ->
                            playlist.playlistId = playlistNumber
                            playlistInteractor.insertPlaylist(playlist)
                        }
                        playlistNumber += 1
                    }
                    playlistInteractor.deletePlaylist(allPlaylists.size + 1)
                }
            }
            if (listOfTracksId.size > 0) {
                closeFragment = true
                deleteUnusedTracks(listOfTracksId)
            } else {
                closeFragmentLiveData.postValue(true)
            }
        }
    }

    private fun deleteUnusedTracks(listOfTracksId: ArrayList<Long>) {
        viewModelScope.launch {
            val listOfTracksIdInAllPlaylists = ArrayList<Long>()
            playlistInteractor.getPlaylists().collect { allPlaylists ->
                allPlaylists.forEach { playlist ->
                    if (playlist.listOfTracksId.isNotEmpty()) {
                        listOfTracksIdInAllPlaylists.addAll(
                            playlistInteractor.getListOfTracksId(
                                playlist.listOfTracksId
                            )
                        )
                    }
                }
                listOfTracksId.forEach { trackId ->
                    if (!listOfTracksIdInAllPlaylists.contains(trackId)) {
                        playlistInteractor.deleteTrack(trackId)
                    }
                }
            }
            closeFragmentLiveData.postValue(closeFragment)
        }
    }

}