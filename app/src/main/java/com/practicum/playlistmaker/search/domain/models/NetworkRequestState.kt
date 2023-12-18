package com.practicum.playlistmaker.search.domain.models

sealed interface NetworkRequestState {
    sealed class OnResponse : NetworkRequestState {
        object ExecutedRequest : OnResponse()
        object IsNotExecutedRequest : OnResponse()
    }

    object OnFailure : NetworkRequestState
    object Default : NetworkRequestState
}