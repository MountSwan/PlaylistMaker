package com.practicum.playlistmaker.library.domain.db

import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

    suspend fun insertPlaylist(playlist: Playlist)

    fun getPlaylists(): Flow<ArrayList<Playlist>>

    fun getListOfTracksId(listOfTracksIdInJson: String): ArrayList<Long>

    fun getTracksIdInPlaylists(): Flow<ArrayList<Long>>

    fun putListOfTracksIdInJson(listOfTracksId: ArrayList<Long>): String

    suspend fun insertTrackInPlaylist(track: Track)

    fun getDataBaseIdOfTracksInPlaylists(): Flow<ArrayList<Int>>

}