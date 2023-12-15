package com.practicum.playlistmaker.main.ui

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel(
    private val searchIntent: Intent,
    private val libraryIntent: Intent,
    private val settingsIntent: Intent,
) :
    ViewModel() {

    private val intentStateLiveData =
        MutableLiveData<Intent>()

    fun observeIntent(): LiveData<Intent> = intentStateLiveData

    fun onClickSearchButton() {
        intentStateLiveData.value = searchIntent
        intentStateLiveData.value = null
    }

    fun onClickMediaLibraryButton() {
        intentStateLiveData.value = libraryIntent
        intentStateLiveData.value = null
    }

    fun onClickSettingsButton() {
        intentStateLiveData.value = settingsIntent
        intentStateLiveData.value = null
    }
}