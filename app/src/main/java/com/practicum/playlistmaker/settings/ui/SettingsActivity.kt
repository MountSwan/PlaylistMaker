package com.practicum.playlistmaker.settings.ui

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var viewModel: SettingsViewModel

    private lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivArrowBack.setOnClickListener {
            finish()
        }

        viewModel = ViewModelProvider(this, SettingsViewModelFactory(applicationContext)).get(
            SettingsViewModel::class.java
        )

        viewModel.getSwitchThemeState(resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK))
        viewModel.observeSwitchThemeState().observe(this) {
            binding.smThemeSwitcher.isChecked = it
        }

        binding.smThemeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
            viewModel.editSharedPrefs(checked)
        }

        binding.ivShare.setOnClickListener {
            viewModel.shareApp()
        }

        binding.ivSupport.setOnClickListener {
            viewModel.getSupport()
        }

        binding.ivUserAgreement.setOnClickListener {
            viewModel.readUserAgreement()
        }
    }
}