package com.practicum.playlistmaker.settings.domain

interface ExternalNavigator {

    fun shareIntent(shareMessage: String)

    fun getSupport()

    fun readUserAgreement()

}