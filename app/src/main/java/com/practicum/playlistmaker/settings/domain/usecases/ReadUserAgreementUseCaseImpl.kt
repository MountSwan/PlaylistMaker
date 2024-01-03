package com.practicum.playlistmaker.settings.domain.usecases

import com.practicum.playlistmaker.settings.domain.ExternalNavigator
import com.practicum.playlistmaker.settings.domain.ReadUserAgreementUseCase

class ReadUserAgreementUseCaseImpl(private val externalNavigator: ExternalNavigator) :
    ReadUserAgreementUseCase {
    override fun execute() {
        externalNavigator.readUserAgreement()
    }
}