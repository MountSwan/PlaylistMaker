package com.practicum.playlistmaker.library.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.library.data.db.entity.TracksInPlaylistsEntity

@Dao
interface TracksInPlaylistsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrackInPlaylist(track: TracksInPlaylistsEntity)

    @Query("SELECT dataBaseId FROM tracks_in_playlists_table")
    suspend fun getDataBaseIdOfTracksInPlaylists(): List<Int>

    @Query("SELECT trackId FROM tracks_in_playlists_table")
    suspend fun getTracksIdInPlaylists(): List<Long>

    @Query("SELECT * FROM tracks_in_playlists_table WHERE trackId=:trackId")
    suspend fun getTrack(trackId: Long): TracksInPlaylistsEntity

    @Query("DELETE FROM tracks_in_playlists_table WHERE trackId=:trackId")
    suspend fun deleteTrack(trackId: Long)

}