package com.practicum.playlistmaker.settings.domain.usecases

import com.practicum.playlistmaker.settings.domain.ExternalNavigator
import com.practicum.playlistmaker.settings.domain.ReadUserAgreement

class ReadUserAgreementUseCase(private val externalNavigator: ExternalNavigator) :
    ReadUserAgreement {
    override fun execute() {
        externalNavigator.readUserAgreement()
    }
}