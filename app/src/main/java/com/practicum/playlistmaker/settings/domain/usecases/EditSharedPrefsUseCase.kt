package com.practicum.playlistmaker.settings.domain.usecases

import com.practicum.playlistmaker.settings.domain.ThemeSwitcherState

class EditSharedPrefsUseCase(private val themeSwitcherState: ThemeSwitcherState) {
    fun execute(checked: Boolean) {
        themeSwitcherState.editSharedPrefs(checked)
    }
}