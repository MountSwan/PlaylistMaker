package com.practicum.playlistmaker.search.domain.usecases

import com.practicum.playlistmaker.search.domain.StartAudioPlayer
import com.practicum.playlistmaker.search.domain.models.Track

class StartAudioPlayerUseCase(private val startAudioPlayer: StartAudioPlayer) {
    fun execute(track: Track) {
        startAudioPlayer.execute(track)
    }
}