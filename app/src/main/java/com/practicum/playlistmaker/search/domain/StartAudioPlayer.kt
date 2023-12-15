package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.search.domain.models.Track

interface StartAudioPlayer {
    fun execute(track: Track)
}