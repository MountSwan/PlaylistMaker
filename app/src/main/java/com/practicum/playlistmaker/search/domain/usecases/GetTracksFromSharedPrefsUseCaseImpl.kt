package com.practicum.playlistmaker.search.domain.usecases

import com.practicum.playlistmaker.search.domain.GetTracksFromSharedPrefsUseCase
import com.practicum.playlistmaker.search.domain.SearchHistoryRepository
import com.practicum.playlistmaker.search.domain.models.Track

class GetTracksFromSharedPrefsUseCaseImpl(private val searchHistory: SearchHistoryRepository) :
    GetTracksFromSharedPrefsUseCase {
    override suspend fun execute(tracksInHistory: ArrayList<Track>) {
        searchHistory.getTracksFromSharedPrefs(tracksInHistory)
    }
}