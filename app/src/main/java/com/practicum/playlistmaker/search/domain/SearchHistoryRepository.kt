package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.search.domain.models.Track

interface SearchHistoryRepository {

    suspend fun getTracksFromSharedPrefs(tracksInHistory: ArrayList<Track>)

    fun addInHistory(
        track: Track,
        tracksInHistory: ArrayList<Track>
    )

    fun clearHistory(tracksInHistory: ArrayList<Track>)

}