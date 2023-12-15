package com.practicum.playlistmaker.main.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainActivityViewModel

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel =
            ViewModelProvider(this, MainActivityViewModelFactory(applicationContext)).get(
                MainActivityViewModel::class.java
            )

        viewModel.observeIntent().observe(this) {
            if (it != null) {
                startActivity(it)
            }
        }

        binding.searchButton.setOnClickListener {
            viewModel.onClickSearchButton()
        }

        binding.mediaLibraryButton.setOnClickListener {
            viewModel.onClickMediaLibraryButton()
        }

        binding.settingsButton.setOnClickListener {
            viewModel.onClickSettingsButton()
        }

    }

}