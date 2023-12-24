package com.practicum.playlistmaker.settings.domain.usecases

import com.practicum.playlistmaker.settings.domain.ExternalNavigator
import com.practicum.playlistmaker.settings.domain.GetSupport

class GetSupportUseCase(private val externalNavigator: ExternalNavigator) : GetSupport {
    override fun execute() {
        externalNavigator.getSupport()
    }
}