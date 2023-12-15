package com.practicum.playlistmaker.settings.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.settings.domain.usecases.EditSharedPrefsUseCase
import com.practicum.playlistmaker.settings.domain.usecases.GetSupportUseCase
import com.practicum.playlistmaker.settings.domain.usecases.ReadUserAgreementUseCase
import com.practicum.playlistmaker.settings.domain.usecases.ShareAppUseCase

class SettingsViewModelFactory(context: Context) : ViewModelProvider.Factory {

    private val themeSwitcherState = Creator.provideThemeSwitcherState(context)
    private val editSharedPrefsUseCase = EditSharedPrefsUseCase(themeSwitcherState)
    private val externalNavigator = Creator.provideExternalNavigator(context)
    private val shareApp = ShareAppUseCase(externalNavigator)
    private val getSupport = GetSupportUseCase(externalNavigator)
    private val readUserAgreement = ReadUserAgreementUseCase(externalNavigator)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingsViewModel(
            editSharedPrefsUseCase = editSharedPrefsUseCase,
            shareApp = shareApp,
            getSupport = getSupport,
            readUserAgreement = readUserAgreement,
        ) as T
    }

}