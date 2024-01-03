package com.practicum.playlistmaker.search.domain.usecases

import com.practicum.playlistmaker.search.domain.SearchTracksUseCase
import com.practicum.playlistmaker.search.domain.TracksRepository
import com.practicum.playlistmaker.search.domain.models.NetworkRequestState
import com.practicum.playlistmaker.search.domain.models.SearchState
import com.practicum.playlistmaker.search.domain.models.Track

class SearchTracksUseCaseImpl(private val tracksRepository: TracksRepository) :
    SearchTracksUseCase {
    override fun execute(
        searchRequest: String,
        searchState: SearchState,
        tracks: ArrayList<Track>
    ): NetworkRequestState {
        return tracksRepository.searchTracks(
            searchRequest = searchRequest,
            searchState = searchState,
            tracks = tracks
        )
    }
}