package com.practicum.playlistmaker.library.data.db

import com.google.gson.Gson
import com.practicum.playlistmaker.library.data.db.converters.PlaylistDbConvertor
import com.practicum.playlistmaker.library.data.db.converters.TrackInPlaylistDbConvertor
import com.practicum.playlistmaker.library.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.library.domain.db.PlaylistRepository
import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConvertor: PlaylistDbConvertor,
    private val trackInPlaylistDbConvertor: TrackInPlaylistDbConvertor,
    private val gson: Gson,
) : PlaylistRepository {

    override suspend fun insertPlaylist(playlist: Playlist) {
        val insertingPlaylist = playlistDbConvertor.map(playlist)
        appDatabase.playlistDao().insertPlaylist(insertingPlaylist)
    }

    override fun getPlaylists(): Flow<ArrayList<Playlist>> = flow {
        val playlists = ArrayList<Playlist>()
        val playlistsInDatabase = appDatabase.playlistDao().getPlaylists()
        playlists.addAll(convertFromPlaylistEntity(playlistsInDatabase))
        emit(playlists)
    }

    override fun getPlaylistInfo(playlistId: Int): Flow<Playlist> = flow {
        val playlist =
            playlistDbConvertor.map(appDatabase.playlistDao().getPlaylistInfo(playlistId))
        emit(playlist)
    }

    override suspend fun deletePlaylist(playlistId: Int) {
        appDatabase.playlistDao().deletePlaylist(playlistId)
    }

    override fun getListOfTracksId(listOfTracksIdInJson: String): ArrayList<Long> {
        val listOfTracksIdFromJson = gson.fromJson(listOfTracksIdInJson, Array<Long>::class.java)
        val listOfTracksId = ArrayList<Long>()
        listOfTracksId.addAll(listOfTracksIdFromJson)
        return listOfTracksId
    }

    override fun getTracksIdInPlaylists(): Flow<ArrayList<Long>> = flow {
        val tracksId = ArrayList<Long>()
        val tracksIdInDatabase = appDatabase.tracksInPlaylistsDao().getTracksIdInPlaylists()
        tracksId.addAll(tracksIdInDatabase)
        emit(tracksId)
    }

    override fun putListOfTracksIdInJson(listOfTracksId: ArrayList<Long>): String {
        return gson.toJson(listOfTracksId)
    }

    override suspend fun insertTrackInPlaylist(track: Track) {
        val insertingTrack = trackInPlaylistDbConvertor.map(track)
        appDatabase.tracksInPlaylistsDao().insertTrackInPlaylist(insertingTrack)
    }

    override fun getDataBaseIdOfTracksInPlaylists(): Flow<ArrayList<Int>> = flow {
        val dataBaseId = ArrayList<Int>()
        val dataBaseIdInDatabase =
            appDatabase.tracksInPlaylistsDao().getDataBaseIdOfTracksInPlaylists()
        dataBaseId.addAll(dataBaseIdInDatabase)
        emit(dataBaseId)
    }

    override fun getTrack(trackId: Long): Flow<Track> = flow {
        val trackInDatabase = appDatabase.tracksInPlaylistsDao().getTrack(trackId)
        emit(trackInPlaylistDbConvertor.map(trackInDatabase))
    }

    override suspend fun deleteTrack(trackId: Long) {
        appDatabase.tracksInPlaylistsDao().deleteTrack(trackId)
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> playlistDbConvertor.map(playlist) }
    }

}