package com.practicum.playlistmaker
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val ivArrowBack = findViewById<ImageView>(R.id.arrow_back_image)
        val ivShare = findViewById<ImageView>(R.id.share)
        val ivSupport = findViewById<ImageView>(R.id.support)
        val ivUserAgreement = findViewById<ImageView>(R.id.user_agreement)

        ivArrowBack.setOnClickListener {
            finish()
        }

        ivShare.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message))
            }
            startActivity(shareIntent)
        }

        ivSupport.setOnClickListener {
            val supportIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.email_address)))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.email_message))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject))
            }
            startActivity(supportIntent)
        }

        ivUserAgreement.setOnClickListener {
            val url = Uri.parse(getString(R.string.url_agreement))
            val userAgreementIntent = Intent(Intent.ACTION_VIEW, url)
            startActivity(userAgreementIntent)
        }
    }
}