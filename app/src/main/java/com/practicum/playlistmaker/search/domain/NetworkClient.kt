package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.search.domain.models.NetworkRequestState
import com.practicum.playlistmaker.search.domain.models.SearchState
import com.practicum.playlistmaker.search.domain.models.Track

interface NetworkClient {

    fun doRequest(
        searchRequest: String, searchState: SearchState,
        tracks: ArrayList<Track>
    )

    fun networkRequestState(): NetworkRequestState

}