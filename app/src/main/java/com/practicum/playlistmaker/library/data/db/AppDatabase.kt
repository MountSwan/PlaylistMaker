package com.practicum.playlistmaker.library.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.library.data.db.dao.FavoriteTrackDao
import com.practicum.playlistmaker.library.data.db.entity.FavoriteTrackEntity

@Database(version = 1, entities = [FavoriteTrackEntity::class])
abstract class AppDatabase : RoomDatabase(){
    abstract fun favoriteTrackDao(): FavoriteTrackDao
}