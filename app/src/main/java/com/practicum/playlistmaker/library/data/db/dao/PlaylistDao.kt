package com.practicum.playlistmaker.library.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.library.data.db.entity.PlaylistEntity

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlist_table")
    suspend fun getPlaylists(): List<PlaylistEntity>

    @Query("SELECT * FROM playlist_table WHERE playlistId=:playlistId")
    suspend fun getPlaylistInfo(playlistId: Int): PlaylistEntity

    @Query("SELECT listOfTracksId FROM playlist_table WHERE playlistId=:playlistId")
    suspend fun getListOfTracksId(playlistId: Int): String

    @Query("DELETE FROM playlist_table WHERE playlistId=:playlistId")
    suspend fun deletePlaylist(playlistId: Int)

}