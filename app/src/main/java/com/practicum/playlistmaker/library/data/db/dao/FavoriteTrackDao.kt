package com.practicum.playlistmaker.library.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.library.data.db.entity.FavoriteTrackEntity

@Dao
interface FavoriteTrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteTrack(track: FavoriteTrackEntity)

    @Delete
    suspend fun deleteFavoriteTrack(track: FavoriteTrackEntity)

    @Query("SELECT * FROM favorite_track_table")
    suspend fun getFavoriteTracks(): List<FavoriteTrackEntity>

    @Query("SELECT trackId FROM favorite_track_table")
    suspend fun getFavoriteTracksId(): List<Long>

}