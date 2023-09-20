package com.practicum.playlistmaker
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val ivArrowBack = findViewById<ImageView>(R.id.arrow_back_image)
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)
        val ivShare = findViewById<ImageView>(R.id.share)
        val ivSupport = findViewById<ImageView>(R.id.support)
        val ivUserAgreement = findViewById<ImageView>(R.id.user_agreement)

        val sharedPrefs = getSharedPreferences(PRACTICUM_EXAMPLE_PREFERENCES, MODE_PRIVATE)

        ivArrowBack.setOnClickListener {
            finish()
        }

        when (resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> themeSwitcher.isChecked = true
            Configuration.UI_MODE_NIGHT_NO -> themeSwitcher.isChecked = false
            Configuration.UI_MODE_NIGHT_UNDEFINED -> themeSwitcher.isChecked = false
        }

        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
            sharedPrefs.edit()
                .putBoolean(EDIT_TEXT_KEY, checked)
                .apply()
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