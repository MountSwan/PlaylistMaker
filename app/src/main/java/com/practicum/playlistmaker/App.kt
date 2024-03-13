package com.practicum.playlistmaker

import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.markodevcic.peko.PermissionRequester
import com.practicum.playlistmaker.di.appModule
import com.practicum.playlistmaker.di.dataModule
import com.practicum.playlistmaker.di.domainModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

const val PRACTICUM_EXAMPLE_PREFERENCES = "practicum_example_preferences"
const val FIRST_TIME_RUN_APP_KEY = "key_for_first_time_run_app"
const val DARK_THEME_STATE_KEY = "key_for_dark_theme_state"
const val HISTORY_SEARCH_KEY = "key_for_history_search"

class App : Application() {

    var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(listOf(appModule, dataModule, domainModule))
        }

        PermissionRequester.initialize(applicationContext)

        val sharedPrefs = getSharedPreferences(PRACTICUM_EXAMPLE_PREFERENCES, MODE_PRIVATE)

        if (sharedPrefs.getBoolean(FIRST_TIME_RUN_APP_KEY, true)) {
            when (resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
                Configuration.UI_MODE_NIGHT_YES -> darkTheme = true
                Configuration.UI_MODE_NIGHT_NO -> darkTheme = false
                Configuration.UI_MODE_NIGHT_UNDEFINED -> darkTheme = false
            }
            setDarkTheme()
            sharedPrefs.edit()
                .putBoolean(FIRST_TIME_RUN_APP_KEY, false)
                .putBoolean(DARK_THEME_STATE_KEY, darkTheme)
                .apply()
        } else {
            darkTheme = sharedPrefs.getBoolean(DARK_THEME_STATE_KEY, darkTheme)
            setDarkTheme()
        }

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