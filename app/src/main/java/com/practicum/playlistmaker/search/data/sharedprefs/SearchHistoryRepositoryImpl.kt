package com.practicum.playlistmaker.search.data.sharedprefs

import com.practicum.playlistmaker.search.data.SearchHistory
import com.practicum.playlistmaker.search.domain.SearchHistoryRepository
import com.practicum.playlistmaker.search.domain.models.Track

class SearchHistoryRepositoryImpl(private val searchHistory: SearchHistory) :
    SearchHistoryRepository {
    override suspend fun getTracksFromSharedPrefs(tracksInHistory: ArrayList<Track>) {
        searchHistory.getTracksFromSharedPrefs(tracksInHistory)
    }

    override fun addInHistory(track: Track, tracksInHistory: ArrayList<Track>) {
        searchHistory.addInHistory(track, tracksInHistory)
    }

    override fun clearHistory(tracksInHistory: ArrayList<Track>) {
        searchHistory.clearHistory(tracksInHistory)
    }
}