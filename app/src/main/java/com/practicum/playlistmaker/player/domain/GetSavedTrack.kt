package com.practicum.playlistmaker.player.domain

import com.practicum.playlistmaker.search.domain.models.Track

interface GetSavedTrack {
    fun execute(): Track?
}