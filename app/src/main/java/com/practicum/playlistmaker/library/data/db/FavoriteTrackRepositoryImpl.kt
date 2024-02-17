package com.practicum.playlistmaker.library.data.db

import com.practicum.playlistmaker.library.data.db.converters.FavoriteTrackDbConvertor
import com.practicum.playlistmaker.library.data.db.entity.FavoriteTrackEntity
import com.practicum.playlistmaker.library.domain.db.FavoriteTrackRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoriteTrackRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val favoriteTrackDbConvertor: FavoriteTrackDbConvertor
) : FavoriteTrackRepository {

    override suspend fun insertFavoriteTrack(track: Track?) {
        val insertingTrack = favoriteTrackDbConvertor.map(track)
        appDatabase.favoriteTrackDao().insertFavoriteTrack(insertingTrack)
    }

    override suspend fun deleteFavoriteTrack(track: Track) {
        val deletingTrack = favoriteTrackDbConvertor.map(track)
        appDatabase.favoriteTrackDao().deleteFavoriteTrack(deletingTrack)
    }

    override fun getFavoriteTracks(): Flow<ArrayList<Track>> = flow {
        val favoriteTracks = ArrayList<Track>()
        val tracks = appDatabase.favoriteTrackDao().getFavoriteTracks()
        favoriteTracks.addAll(convertFromFavoriteTrackEntity(tracks))
        emit(favoriteTracks)
    }

    override suspend fun checkIsFavorite(trackID: Long?): Boolean {
        appDatabase.favoriteTrackDao().getFavoriteTracksId()
            .forEach { favoriteTrackId -> if (favoriteTrackId == trackID) return true }
        return false
    }

    private fun convertFromFavoriteTrackEntity(tracks: List<FavoriteTrackEntity>): List<Track> {
        return tracks.map { track -> favoriteTrackDbConvertor.map(track) }
    }

}