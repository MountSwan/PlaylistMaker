package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.search.domain.models.NetworkRequestState
import com.practicum.playlistmaker.search.domain.models.SearchState
import com.practicum.playlistmaker.search.domain.models.Track

interface TracksRepository {

    fun searchTracks(
        searchRequest: String, searchState: SearchState,
        tracks: ArrayList<Track>
    )

    fun networkRequestState(): NetworkRequestState

}