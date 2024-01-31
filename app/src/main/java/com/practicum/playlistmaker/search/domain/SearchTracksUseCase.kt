package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.search.domain.models.NetworkRequestState
import com.practicum.playlistmaker.search.domain.models.SearchState
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface SearchTracksUseCase {
    fun execute(
        searchRequest: String,
        searchState: SearchState,
        tracks: ArrayList<Track>
    ): Flow<NetworkRequestState>
}