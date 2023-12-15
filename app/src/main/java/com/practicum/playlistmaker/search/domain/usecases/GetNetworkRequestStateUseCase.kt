package com.practicum.playlistmaker.search.domain.usecases

import com.practicum.playlistmaker.search.domain.NetworkClient
import com.practicum.playlistmaker.search.domain.models.NetworkRequestState

class GetNetworkRequestStateUseCase(private val networkClient: NetworkClient) {
    fun execute(): NetworkRequestState {
        return networkClient.networkRequestState()
    }
}