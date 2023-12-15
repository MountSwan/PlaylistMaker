package com.practicum.playlistmaker.settings.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.settings.domain.ExternalNavigator

class ExternalNavigatorImpl(private val context: Context): ExternalNavigator {

    override fun shareIntent() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_message))
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(shareIntent)
    }

    override fun getSupport() {
        val supportIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.email_address)))
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.email_message))
            putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.email_subject))
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(supportIntent)
    }

    override fun readUserAgreement() {
        val url = Uri.parse(context.getString(R.string.url_agreement))
        val userAgreementIntent = Intent(Intent.ACTION_VIEW, url)
        userAgreementIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(userAgreementIntent)
    }


}