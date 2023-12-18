package com.practicum.playlistmaker.search.domain.usecases

import com.practicum.playlistmaker.search.domain.TracksRepository
import com.practicum.playlistmaker.search.domain.models.NetworkRequestState

class GetNetworkRequestStateUseCase(private val tracksRepository: TracksRepository) {
    fun execute(): NetworkRequestState {
        return tracksRepository.networkRequestState()
    }
}