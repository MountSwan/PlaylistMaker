package com.practicum.playlistmaker.domain.usecases

import com.practicum.playlistmaker.domain.SetIVControlPlay

class SetIVControlPlayUseCase(private val setIVControlPlay: SetIVControlPlay) {
    fun execute(imageCode: Int) {
        setIVControlPlay.execute(imageCode)
    }
}