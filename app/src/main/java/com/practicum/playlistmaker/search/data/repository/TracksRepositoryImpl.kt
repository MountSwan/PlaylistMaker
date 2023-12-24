package com.practicum.playlistmaker.search.data.repository

import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.models.TrackDto
import com.practicum.playlistmaker.search.domain.TracksRepository
import com.practicum.playlistmaker.search.domain.models.NetworkRequestState
import com.practicum.playlistmaker.search.domain.models.SearchState
import com.practicum.playlistmaker.search.domain.models.Track

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    private val tracksResponse = ArrayList<TrackDto>()

    override fun searchTracks(
        searchRequest: String,
        searchState: SearchState,
        tracks: ArrayList<Track>
    ): NetworkRequestState {
        val networkRequestState =
            networkClient.doRequest(searchRequest, searchState, tracks, tracksResponse)
        if (tracksResponse.isNotEmpty()) {
            tracks.addAll(tracksResponse.map {
                Track(
                    trackId = it.trackId,
                    trackName = it.trackName,
                    artistName = it.artistName,
                    trackTimeMillis = it.trackTimeMillis,
                    trackTime = it.trackTime,
                    artworkUrl100 = it.artworkUrl100,
                    artworkUrl512 = it.artworkUrl512,
                    collectionName = it.collectionName,
                    releaseDate = it.releaseDate,
                    primaryGenreName = it.primaryGenreName,
                    country = it.country,
                    previewUrl = it.previewUrl
                )
            })

            searchState.responseResultsIsNotEmpty = true
        }
        return networkRequestState
    }

}