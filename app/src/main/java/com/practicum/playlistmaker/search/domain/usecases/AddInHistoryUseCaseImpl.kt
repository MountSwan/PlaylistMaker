package com.practicum.playlistmaker.search.domain.usecases

import com.practicum.playlistmaker.search.domain.AddInHistoryUseCase
import com.practicum.playlistmaker.search.domain.SearchHistoryRepository
import com.practicum.playlistmaker.search.domain.models.Track

class AddInHistoryUseCaseImpl(private val searchHistory: SearchHistoryRepository) :
    AddInHistoryUseCase {
    override fun execute(
        track: Track,
        tracksInHistory: ArrayList<Track>
    ) {
        searchHistory.addInHistory(track, tracksInHistory)
    }
}