package com.practicum.playlistmaker.player.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityAudioplayerBinding
import com.practicum.playlistmaker.player.domain.models.MediaPlayerState
import androidx.lifecycle.ViewModelProvider

class AudioPlayerActivity : AppCompatActivity() {

    private val mainThreadHandler = Handler(Looper.getMainLooper())

    private lateinit var viewModel: AudioPlayerViewModel

    private lateinit var binding: ActivityAudioplayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioplayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.arrowBackImage.setOnClickListener {
            finish()
        }

        viewModel =
            ViewModelProvider(this, AudioPlayerViewModelFactory(mainThreadHandler, intent)).get(
                AudioPlayerViewModel::class.java
            )

        viewModel.observeSavedTrack().observe(this) {
            Glide.with(applicationContext)
                .load(it?.artworkUrl512)
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .transform(RoundedCorners(applicationContext.resources.getDimensionPixelSize(R.dimen.two_space)))
                .into(binding.album)
            binding.trackName.text = it?.trackName
            binding.artistName.text = it?.artistName
            binding.trackTime.text = it?.trackTime
            binding.collection.text = it?.collectionName
            binding.collectionGroup.isVisible = it?.collectionName?.isNotEmpty() == true
            binding.year.text = it?.releaseDate?.take(FIRST_FOUR_CHARACTERS)
            binding.genre.text = it?.primaryGenreName
            binding.country.text = it?.country
        }

        viewModel.observePlayerState().observe(this) {
            when (it) {
                is MediaPlayerState.Prepared.OnPrepared -> {
                    binding.controlPlay.isEnabled = true
                    viewModel.defineMediaPlayerStatePreparedAsDefault()
                }

                is MediaPlayerState.Prepared.OnCompletion -> {
                    binding.controlPlay.setImageResource(R.drawable.control_play)
                    binding.timePlayTrack.text = "00:00"
                    viewModel.defineMediaPlayerStatePreparedAsDefault()
                }

                is MediaPlayerState.Playing -> {
                    binding.controlPlay.setImageResource(R.drawable.control_pause)
                }

                is MediaPlayerState.Paused -> {
                    binding.controlPlay.setImageResource(R.drawable.control_play)
                }

                is MediaPlayerState.Prepared.Default, MediaPlayerState.Default -> Unit
            }
        }

        viewModel.observeTimePlayTrack().observe(this) {
            binding.timePlayTrack.text = it
        }

        viewModel.preparePlayer()

        binding.controlPlay.setOnClickListener {
            viewModel.playbackControl()
        }

    }

    override fun onPause() {
        binding.controlPlay.setImageResource(R.drawable.control_play)
        viewModel.pausePlayer()
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.releasePlayer()
    }

    companion object {
        const val FIRST_FOUR_CHARACTERS = 4
    }

}