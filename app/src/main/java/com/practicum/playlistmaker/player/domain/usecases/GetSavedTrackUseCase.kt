package com.practicum.playlistmaker.player.domain.usecases

import com.practicum.playlistmaker.player.domain.GetSavedTrack
import com.practicum.playlistmaker.search.domain.models.Track

class GetSavedTrackUseCase(private val getSavedTrack: GetSavedTrack) {
    fun execute(): Track? {
        return getSavedTrack.execute()
    }
}