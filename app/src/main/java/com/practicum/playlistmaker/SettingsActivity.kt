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

        val arrowBackImage = findViewById<ImageView>(R.id.arrow_back_image)
        val shareImage = findViewById<ImageView>(R.id.share)
        val supportImage = findViewById<ImageView>(R.id.support)
        val userAgreementImage = findViewById<ImageView>(R.id.user_agreement)

        arrowBackImage.setOnClickListener {
            finish()
        }

        shareImage.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message))
            startActivity(shareIntent)
        }

        supportImage.setOnClickListener {
            val supportIntent = Intent(Intent.ACTION_SENDTO)
            supportIntent.data = Uri.parse("mailto:")
            supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.email_address)))
            supportIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.email_message))
            supportIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject))
            startActivity(supportIntent)
        }

        userAgreementImage.setOnClickListener {
            val url = Uri.parse(getString(R.string.url_agreement))
            val userAgreementIntent = Intent(Intent.ACTION_VIEW, url)
            startActivity(userAgreementIntent)
        }
    }
}