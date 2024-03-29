package com.practicum.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.domain.db.FavoriteTrackInteractor
import com.practicum.playlistmaker.library.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.library.ui.PlaylistUI
import com.practicum.playlistmaker.player.domain.MediaPlayerInteractor
import com.practicum.playlistmaker.player.domain.models.MediaPlayerState
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerViewModel(
    private val savedTrack: Track?,
    private val mediaPlayerInteractor: MediaPlayerInteractor,
    private val favoriteTrackInteractor: FavoriteTrackInteractor,
    private val playlistInteractor: PlaylistInteractor,
) :
    ViewModel() {

    private val savedTrackLiveData =
        MutableLiveData<Track?>()

    fun observeSavedTrack(): LiveData<Track?> = savedTrackLiveData

    private val playerStateLiveData =
        MutableLiveData<MediaPlayerState>()

    fun observePlayerState(): LiveData<MediaPlayerState> = playerStateLiveData

    private val timePlayTrackLiveData = MutableLiveData<String>()
    fun observeTimePlayTrack(): LiveData<String> = timePlayTrackLiveData

    private val playlistsFromDatabaseLiveData = MutableLiveData<ArrayList<Playlist>>()
    fun observePlaylistsFromDatabase(): LiveData<ArrayList<Playlist>> =
        playlistsFromDatabaseLiveData

    private val resultOfAddingTrackInPlaylistLiveData = MutableLiveData<Boolean>()
    fun observeResultOfAddingTrackInPlaylist(): LiveData<Boolean> =
        resultOfAddingTrackInPlaylistLiveData

    private var timerJob: Job? = null
    private var listenerPlayerStateJob: Job? = null

    init {
        savedTrackLiveData.value = savedTrack
    }

    fun defineMediaPlayerStatePreparedAsDefault() {
        mediaPlayerInteractor.defineMediaPlayerStatePreparedAsDefault()
    }

    fun preparePlayer() {
        mediaPlayerInteractor.preparePlayer(savedTrack?.previewUrl)
    }

    fun playbackControl() {
        when (mediaPlayerInteractor.playerState()) {
            is MediaPlayerState.Playing -> {
                pausePlayer()
            }

            is MediaPlayerState.Prepared, MediaPlayerState.Paused -> {
                startPlayer()
            }

            is MediaPlayerState.Default -> Unit
        }
    }

    private fun startPlayer() {
        mediaPlayerInteractor.startPlayer()
        timerJob = viewModelScope.launch {
            while (mediaPlayerInteractor.playerState() == MediaPlayerState.Playing) {
                delay(REFRESH_TIMER_MILLIS)
                timePlayTrackLiveData.value =
                    SimpleDateFormat(
                        "mm:ss",
                        Locale.getDefault()
                    ).format(mediaPlayerInteractor.playerCurrentPosition())
            }
        }

    }

    fun pausePlayer() {
        mediaPlayerInteractor.pausePlayer()
        timerJob?.cancel()
    }

    fun releasePlayer() {
        timerJob?.cancel()
        listenerPlayerStateJob?.cancel()
        mediaPlayerInteractor.releasePlayer()
    }

    fun startListenerPlayerStateJob() {
        playerStateLiveData.value = mediaPlayerInteractor.playerState()
        listenerPlayerStateJob = viewModelScope.launch {
            while (true) {
                delay(REFRESH_TIMER_MILLIS)
                when (mediaPlayerInteractor.playerState()) {
                    is MediaPlayerState.Prepared.OnCompletion -> {
                        timerJob?.cancel()
                    }

                    else -> Unit
                }
                playerStateLiveData.value = mediaPlayerInteractor.playerState()
            }
        }
    }

    fun onFavoriteClicked() {
        viewModelScope.launch {
            if (savedTrack?.isFavorite == true) {
                favoriteTrackInteractor.getFavoriteTracks().collect {
                    val reSortedFavoriteTracks = ArrayList<Track>()
                    it.forEach { track -> reSortedFavoriteTracks.add(0, track) }
                    var trackIsDeleted = false
                    reSortedFavoriteTracks.forEach { track ->
                        if (trackIsDeleted) {
                            track.dataBaseId = track.dataBaseId?.minus(1)
                            favoriteTrackInteractor.insertFavoriteTrack(track)
                            if (track.dataBaseId == (it.size - 1)) {
                                track.dataBaseId = track.dataBaseId!! + 1
                                favoriteTrackInteractor.deleteFavoriteTrack(track)
                            }
                        }

                        if (track.trackId == savedTrack.trackId) {
                            savedTrack.dataBaseId = track.dataBaseId
                            favoriteTrackInteractor.deleteFavoriteTrack(savedTrack)
                            trackIsDeleted = true
                        }
                    }
                    savedTrack.isFavorite = false
                    savedTrackLiveData.postValue(savedTrack)
                }
            } else {
                savedTrack?.isFavorite = true
                favoriteTrackInteractor.getFavoriteTracks().collect {
                    savedTrack?.dataBaseId = it.size + 1
                    favoriteTrackInteractor.insertFavoriteTrack(savedTrack)
                    savedTrackLiveData.postValue(savedTrack)
                }
            }
        }
    }

    fun getPlaylistsFromDatabase() {
        viewModelScope.launch {
            playlistInteractor.getPlaylists().collect { playlistsInDatabase ->
                playlistsFromDatabaseLiveData.postValue(playlistsInDatabase)
            }
        }
    }

    fun addTrackInPlaylist(playlistUI: PlaylistUI) {
        val listOfTracksIdInPlaylist = ArrayList<Long>()
        if (playlistUI.listOfTracksId.isNotEmpty()) {
            listOfTracksIdInPlaylist.addAll(playlistInteractor.getListOfTracksId(playlistUI.listOfTracksId))
        }
        if (listOfTracksIdInPlaylist.indexOf(savedTrack?.trackId) != -1) {
            resultOfAddingTrackInPlaylistLiveData.value = false
        } else {
            savedTrack?.trackId?.let { listOfTracksIdInPlaylist.add(it) }
            val listOfTracksIdString =
                playlistInteractor.putListOfTracksIdInJson(listOfTracksIdInPlaylist)
            val playlist = Playlist(
                playlistId = playlistUI.playlistId,
                playlistName = playlistUI.playlistName,
                playlistDescription = playlistUI.playlistDescription,
                listOfTracksId = listOfTracksIdString,
                numberOfTracksInPlaylist = playlistUI.numberOfTracksInPlaylist + 1,
            )
            viewModelScope.launch {
                playlistInteractor.insertPlaylist(playlist)
                playlistInteractor.getTracksIdInPlaylists().collect { tracksIdInPlaylists ->
                    if (tracksIdInPlaylists.indexOf(savedTrack?.trackId) == -1) {
                        val addingTrack = savedTrack
                        playlistInteractor.getDataBaseIdOfTracksInPlaylists()
                            .collect { dataBaseIdOfTracksInPlaylists ->
                                if (addingTrack != null) {
                                    addingTrack.dataBaseId = dataBaseIdOfTracksInPlaylists.size + 1
                                    playlistInteractor.insertTrackInPlaylist(addingTrack)
                                }
                            }
                    }
                    resultOfAddingTrackInPlaylistLiveData.postValue(true)
                }
            }
        }

    }

    companion object {
        private const val REFRESH_TIMER_MILLIS = 300L
    }

}