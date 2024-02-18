package com.practicum.playlistmaker.library.domain.db

import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTrackRepository {

    suspend fun insertFavoriteTrack(track: Track)

    suspend fun deleteFavoriteTrack(track: Track)

    fun getFavoriteTracks(): Flow<ArrayList<Track>>

}