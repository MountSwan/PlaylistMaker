package com.practicum.playlistmaker.settings.domain.usecases

import com.practicum.playlistmaker.settings.domain.ExternalNavigator
import com.practicum.playlistmaker.settings.domain.ShareAppUseCase

class ShareAppUseCaseImpl(private val externalNavigator: ExternalNavigator):
    ShareAppUseCase {
    override fun execute() {
        externalNavigator.shareIntent()
    }
}