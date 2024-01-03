package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.search.domain.models.Track

interface ClearHistoryUseCase {
    fun execute(tracksInHistory: ArrayList<Track>)
}