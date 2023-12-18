package com.practicum.playlistmaker.settings.data.sharedprefs

import android.content.Context
import com.practicum.playlistmaker.EDIT_TEXT_KEY
import com.practicum.playlistmaker.PRACTICUM_EXAMPLE_PREFERENCES
import com.practicum.playlistmaker.settings.domain.ThemeSwitcherState

class ThemeSwitcherStateImpl(context: Context): ThemeSwitcherState {

    val sharedPrefs = context.getSharedPreferences(
        PRACTICUM_EXAMPLE_PREFERENCES,
        Context.MODE_PRIVATE
    )

    override fun editSharedPrefs(checked: Boolean) {
        sharedPrefs.edit()
            .putBoolean(EDIT_TEXT_KEY, checked)
            .apply()
    }
}