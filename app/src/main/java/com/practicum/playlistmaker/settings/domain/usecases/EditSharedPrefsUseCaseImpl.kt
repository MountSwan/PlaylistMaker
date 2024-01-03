package com.practicum.playlistmaker.settings.domain.usecases

import com.practicum.playlistmaker.settings.domain.EditSharedPrefsUseCase
import com.practicum.playlistmaker.settings.domain.ThemeSwitcherState

class EditSharedPrefsUseCaseImpl(private val themeSwitcherState: ThemeSwitcherState) :
    EditSharedPrefsUseCase {
    override fun execute(checked: Boolean) {
        themeSwitcherState.editSharedPrefs(checked)
    }
}