package com.practicum.playlistmaker.domain.usecases

import com.practicum.playlistmaker.domain.EnableIVControlPlay

class EnableIVControlPlayUseCase(private val enableIVControlPlay: EnableIVControlPlay) {
    fun execute() {
        enableIVControlPlay.execute()
    }
}