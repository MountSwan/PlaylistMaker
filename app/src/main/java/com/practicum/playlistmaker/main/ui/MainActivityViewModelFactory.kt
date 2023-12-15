package com.practicum.playlistmaker.main.ui

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.LibraryActivity
import com.practicum.playlistmaker.search.ui.SearchActivity
import com.practicum.playlistmaker.settings.ui.SettingsActivity

class MainActivityViewModelFactory(context: Context): ViewModelProvider.Factory {

    private val searchIntent = Intent(context, SearchActivity::class.java)
    private val libraryIntent = Intent(context, LibraryActivity::class.java)
    private val settingsIntent = Intent(context, SettingsActivity::class.java)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainActivityViewModel(
            searchIntent = searchIntent,
            libraryIntent = libraryIntent,
            settingsIntent = settingsIntent,
        ) as T
    }
}