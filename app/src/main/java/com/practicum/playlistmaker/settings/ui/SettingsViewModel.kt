package com.practicum.playlistmaker.settings.ui

import android.content.res.Configuration
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.settings.domain.EditSharedPrefs
import com.practicum.playlistmaker.settings.domain.GetSupport
import com.practicum.playlistmaker.settings.domain.ReadUserAgreement
import com.practicum.playlistmaker.settings.domain.ShareApp

class SettingsViewModel(
    private val editSharedPrefs: EditSharedPrefs,
    private val shareApp: ShareApp,
    private val getSupport: GetSupport,
    private val readUserAgreement: ReadUserAgreement,
) :
    ViewModel() {

    private val switchThemeStateLiveData =
        MutableLiveData<Boolean>()

    fun observeSwitchThemeState(): LiveData<Boolean> = switchThemeStateLiveData

    fun getSwitchThemeState(uiModeNight: Int) {
        when (uiModeNight) {
            Configuration.UI_MODE_NIGHT_YES -> switchThemeStateLiveData.value = true
            Configuration.UI_MODE_NIGHT_NO -> switchThemeStateLiveData.value = false
            Configuration.UI_MODE_NIGHT_UNDEFINED -> switchThemeStateLiveData.value = false
        }
    }

    fun editSharedPrefs(checked: Boolean) {
        editSharedPrefs.execute(checked)
    }

    fun shareApp() {
        shareApp.execute()
    }

    fun getSupport() {
        getSupport.execute()
    }

    fun readUserAgreement() {
        readUserAgreement.execute()
    }

}