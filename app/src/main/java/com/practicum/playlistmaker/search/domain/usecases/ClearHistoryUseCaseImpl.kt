package com.practicum.playlistmaker.search.domain.usecases

import com.practicum.playlistmaker.search.domain.ClearHistoryUseCase
import com.practicum.playlistmaker.search.domain.SearchHistoryRepository
import com.practicum.playlistmaker.search.domain.models.Track

class ClearHistoryUseCaseImpl(private val searchHistory: SearchHistoryRepository) :
    ClearHistoryUseCase {
    override fun execute(tracksInHistory: ArrayList<Track>) {
        searchHistory.clearHistory(tracksInHistory)
    }
}