package com.practicum.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.di.appModule
import com.practicum.playlistmaker.di.dataModule
import com.practicum.playlistmaker.di.domainModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

const val PRACTICUM_EXAMPLE_PREFERENCES = "practicum_example_preferences"
const val EDIT_TEXT_KEY = "key_for_edit_text"
const val HISTORY_SEARCH_KEY = "key_for_history_search"
const val SAVE_TRACK_FOR_AUDIO_PLAYER_KEY = "key_for_save_track_for_audio_player"

class App : Application() {

    var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        startKoin{
            androidContext(this@App)
            modules(listOf(appModule, dataModule, domainModule))
        }

        val sharedPrefs = getSharedPreferences(PRACTICUM_EXAMPLE_PREFERENCES, MODE_PRIVATE)

        darkTheme = sharedPrefs.getBoolean(EDIT_TEXT_KEY, darkTheme)
        setDarkTheme()

    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        setDarkTheme()
    }

    private fun setDarkTheme() {
        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }


}