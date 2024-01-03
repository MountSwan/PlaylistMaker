package com.practicum.playlistmaker.settings.ui

import android.content.res.Configuration
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.settings.domain.EditSharedPrefsUseCase
import com.practicum.playlistmaker.settings.domain.GetSupportUseCase
import com.practicum.playlistmaker.settings.domain.ReadUserAgreementUseCase
import com.practicum.playlistmaker.settings.domain.ShareAppUseCase

class SettingsViewModel(
    private val editSharedPrefs: EditSharedPrefsUseCase,
    private val shareApp: ShareAppUseCase,
    private val getSupport: GetSupportUseCase,
    private val readUserAgreement: ReadUserAgreementUseCase,
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