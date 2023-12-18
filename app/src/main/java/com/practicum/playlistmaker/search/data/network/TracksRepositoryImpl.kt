package com.practicum.playlistmaker.search.data.network

import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.domain.TracksRepository
import com.practicum.playlistmaker.search.domain.models.NetworkRequestState
import com.practicum.playlistmaker.search.domain.models.SearchState
import com.practicum.playlistmaker.search.domain.models.Track

class TracksRepositoryImpl(private val networkClient: NetworkClient): TracksRepository {
    override fun searchTracks(
        searchRequest: String,
        searchState: SearchState,
        tracks: ArrayList<Track>
    ) {
        networkClient.doRequest(searchRequest, searchState, tracks)
    }

    override fun networkRequestState(): NetworkRequestState {
        return networkClient.networkRequestState()
    }

}