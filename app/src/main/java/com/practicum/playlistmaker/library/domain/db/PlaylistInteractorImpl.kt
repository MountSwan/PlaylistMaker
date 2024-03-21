package com.practicum.playlistmaker.library.domain.db

import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val playlistRepository: PlaylistRepository) :
    PlaylistInteractor {

    override suspend fun insertPlaylist(playlist: Playlist) {
        playlistRepository.insertPlaylist(playlist)
    }

    override fun getPlaylists(): Flow<ArrayList<Playlist>> {
        return playlistRepository.getPlaylists()
    }

    override fun getPlaylistInfo(playlistId: Int): Flow<Playlist> {
        return playlistRepository.getPlaylistInfo(playlistId)
    }

    override suspend fun deletePlaylist(playlistId: Int) {
        playlistRepository.deletePlaylist(playlistId)
    }

    override fun getListOfTracksId(listOfTracksIdInJson: String): ArrayList<Long> {
        return playlistRepository.getListOfTracksId(listOfTracksIdInJson)
    }

    override fun putListOfTracksIdInJson(listOfTracksId: ArrayList<Long>): String {
        return playlistRepository.putListOfTracksIdInJson(listOfTracksId)
    }

    override fun getTracksIdInPlaylists(): Flow<ArrayList<Long>> {
        return playlistRepository.getTracksIdInPlaylists()
    }

    override suspend fun insertTrackInPlaylist(track: Track) {
        playlistRepository.insertTrackInPlaylist(track)
    }

    override fun getDataBaseIdOfTracksInPlaylists(): Flow<ArrayList<Int>> {
        return playlistRepository.getDataBaseIdOfTracksInPlaylists()
    }

    override fun getTrack(trackId: Long): Flow<Track> {
        return playlistRepository.getTrack(trackId)
    }

    override suspend fun deleteTrack(trackId: Long) {
        playlistRepository.deleteTrack(trackId)
    }
}