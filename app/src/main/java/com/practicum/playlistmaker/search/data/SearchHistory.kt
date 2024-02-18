package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.search.domain.models.Track

interface SearchHistory {

    suspend fun getTracksFromSharedPrefs(tracksInHistory: ArrayList<Track>)

    fun addInHistory(
        track: Track,
        tracksInHistory: ArrayList<Track>
    )

    fun clearHistory(tracksInHistory: ArrayList<Track>)

}