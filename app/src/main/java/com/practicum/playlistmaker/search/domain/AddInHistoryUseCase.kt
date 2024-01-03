package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.search.domain.models.Track

interface AddInHistoryUseCase {
    fun execute(
        track: Track,
        tracksInHistory: ArrayList<Track>
    )
}