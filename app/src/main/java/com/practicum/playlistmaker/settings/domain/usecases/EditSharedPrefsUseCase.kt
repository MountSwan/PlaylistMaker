package com.practicum.playlistmaker.settings.domain.usecases

import com.practicum.playlistmaker.settings.domain.EditSharedPrefs
import com.practicum.playlistmaker.settings.domain.ThemeSwitcherState

class EditSharedPrefsUseCase(private val themeSwitcherState: ThemeSwitcherState) : EditSharedPrefs {
    override fun execute(checked: Boolean) {
        themeSwitcherState.editSharedPrefs(checked)
    }
}