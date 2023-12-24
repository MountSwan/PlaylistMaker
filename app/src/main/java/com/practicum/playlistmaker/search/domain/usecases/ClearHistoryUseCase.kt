package com.practicum.playlistmaker.search.domain.usecases

import com.practicum.playlistmaker.search.domain.SearchHistoryRepository
import com.practicum.playlistmaker.search.domain.models.Track

class ClearHistoryUseCase(private val searchHistory: SearchHistoryRepository) {
    fun execute(tracksInHistory: ArrayList<Track>) {
        searchHistory.clearHistory(tracksInHistory)
    }
}