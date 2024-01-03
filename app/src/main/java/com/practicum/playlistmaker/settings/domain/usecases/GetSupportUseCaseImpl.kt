package com.practicum.playlistmaker.settings.domain.usecases

import com.practicum.playlistmaker.settings.domain.ExternalNavigator
import com.practicum.playlistmaker.settings.domain.GetSupportUseCase

class GetSupportUseCaseImpl(private val externalNavigator: ExternalNavigator) :
    GetSupportUseCase {
    override fun execute() {
        externalNavigator.getSupport()
    }
}