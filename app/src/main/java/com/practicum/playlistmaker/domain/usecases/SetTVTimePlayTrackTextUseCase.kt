package com.practicum.playlistmaker.domain.usecases

import com.practicum.playlistmaker.domain.SetTVTimePlayTrackText

class SetTVTimePlayTrackTextUseCase(private val setTVTimePlayTrackText: SetTVTimePlayTrackText) {
    fun execute(text: String) {
        setTVTimePlayTrackText.execute(text)
    }
}