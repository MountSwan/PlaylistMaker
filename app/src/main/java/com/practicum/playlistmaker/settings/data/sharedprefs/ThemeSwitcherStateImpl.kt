package com.practicum.playlistmaker.settings.data.sharedprefs

import android.content.SharedPreferences
import com.practicum.playlistmaker.DARK_THEME_STATE_KEY
import com.practicum.playlistmaker.settings.domain.ThemeSwitcherState

class ThemeSwitcherStateImpl(private val sharedPrefs: SharedPreferences) : ThemeSwitcherState {

    override fun editSharedPrefs(checked: Boolean) {
        sharedPrefs.edit()
            .putBoolean(DARK_THEME_STATE_KEY, checked)
            .apply()
    }
}