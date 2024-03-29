package com.practicum.playlistmaker.library.domain.db

import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {

    suspend fun insertPlaylist(playlist: Playlist)

    fun getPlaylists(): Flow<ArrayList<Playlist>>

    fun getPlaylistInfo(playlistId: Int): Flow<Playlist>

    suspend fun deletePlaylist(playlistId: Int)

    fun getListOfTracksId(listOfTracksIdInJson: String): ArrayList<Long>

    fun putListOfTracksIdInJson(listOfTracksId: ArrayList<Long>): String

    fun getTracksIdInPlaylists(): Flow<ArrayList<Long>>

    suspend fun insertTrackInPlaylist(track: Track)

    fun getDataBaseIdOfTracksInPlaylists(): Flow<ArrayList<Int>>

    fun getTrack(trackId: Long): Flow<Track>

    suspend fun deleteTrack(trackId: Long)

}