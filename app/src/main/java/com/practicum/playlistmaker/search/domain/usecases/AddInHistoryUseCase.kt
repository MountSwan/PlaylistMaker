package com.practicum.playlistmaker.search.domain.usecases

import com.practicum.playlistmaker.search.domain.AddInHistory
import com.practicum.playlistmaker.search.domain.SearchHistoryRepository
import com.practicum.playlistmaker.search.domain.models.Track

class AddInHistoryUseCase(private val searchHistory: SearchHistoryRepository) : AddInHistory {
    override fun execute(
        track: Track,
        tracksInHistory: ArrayList<Track>
    ) {
        searchHistory.addInHistory(track, tracksInHistory)
    }
}