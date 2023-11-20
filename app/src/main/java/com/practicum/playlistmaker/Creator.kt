package com.practicum.playlistmaker

import com.practicum.playlistmaker.data.repository.AudioPlayerImpl
import com.practicum.playlistmaker.domain.AudioPlayer

object Creator {

    fun provideAudioPlayer(): AudioPlayer {
        return AudioPlayerImpl()
    }

}