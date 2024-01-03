package com.practicum.playlistmaker.settings.domain

interface EditSharedPrefsUseCase {
    fun execute(checked: Boolean)
}