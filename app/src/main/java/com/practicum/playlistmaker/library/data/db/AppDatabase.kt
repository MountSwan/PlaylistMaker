package com.practicum.playlistmaker.library.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.library.data.db.dao.FavoriteTrackDao
import com.practicum.playlistmaker.library.data.db.dao.PlaylistDao
import com.practicum.playlistmaker.library.data.db.dao.TracksInPlaylistsDao
import com.practicum.playlistmaker.library.data.db.entity.FavoriteTrackEntity
import com.practicum.playlistmaker.library.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.library.data.db.entity.TracksInPlaylistsEntity

@Database(
    version = 1,
    entities = [FavoriteTrackEntity::class, PlaylistEntity::class, TracksInPlaylistsEntity::class]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteTrackDao(): FavoriteTrackDao

    abstract fun playlistDao(): PlaylistDao

    abstract fun tracksInPlaylistsDao(): TracksInPlaylistsDao
}