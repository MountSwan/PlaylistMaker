package com.practicum.playlistmaker.settings.domain.usecases

import com.practicum.playlistmaker.settings.domain.ExternalNavigator

class ReadUserAgreementUseCase(private val externalNavigator: ExternalNavigator) {
    fun execute() {
        externalNavigator.readUserAgreement()
    }
}