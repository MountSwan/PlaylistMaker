package com.practicum.playlistmaker.settings.domain.usecases

import com.practicum.playlistmaker.settings.domain.ExternalNavigator
import com.practicum.playlistmaker.settings.domain.ShareApp

class ShareAppUseCase(private val externalNavigator: ExternalNavigator): ShareApp {
    override fun execute() {
        externalNavigator.shareIntent()
    }
}