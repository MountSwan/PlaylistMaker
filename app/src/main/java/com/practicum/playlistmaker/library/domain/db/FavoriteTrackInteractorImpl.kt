package com.practicum.playlistmaker.library.domain.db

import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoriteTrackInteractorImpl(private val favoriteTrackRepository: FavoriteTrackRepository) :
    FavoriteTrackInteractor {
    override suspend fun insertFavoriteTrack(track: Track?) {
        favoriteTrackRepository.insertFavoriteTrack(track)
    }

    override suspend fun deleteFavoriteTrack(track: Track) {
        favoriteTrackRepository.deleteFavoriteTrack(track)
    }

    override fun getFavoriteTracks(): Flow<ArrayList<Track>> = flow {
        val sortedFavoriteTracks = ArrayList<Track>()
        favoriteTrackRepository.getFavoriteTracks().collect {
            it.forEach { track -> sortedFavoriteTracks.add(0, track) }
        }
        emit(sortedFavoriteTracks)
    }

    override suspend fun checkIsFavorite(trackID: Long?): Boolean {
        return favoriteTrackRepository.checkIsFavorite(trackID)
    }

}