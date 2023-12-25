package com.practicum.playlistmaker.player.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityAudioplayerBinding
import com.practicum.playlistmaker.player.domain.models.MediaPlayerState
import com.practicum.playlistmaker.search.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel

class AudioPlayerActivity : AppCompatActivity() {

    private val viewModel by viewModel<AudioPlayerViewModel>()

    private lateinit var binding: ActivityAudioplayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioplayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.arrowBackImage.setOnClickListener {
            finish()
        }

        viewModel.getSavedTrack(intent)

        viewModel.observeSavedTrack().observe(this) {
            drawScreen(it)
        }

        viewModel.observePlayerState().observe(this) {
            displayPlayer(it)
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

    private fun drawScreen(savedTrack: Track?) {
        Glide.with(applicationContext)
            .load(savedTrack?.artworkUrl512)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(applicationContext.resources.getDimensionPixelSize(R.dimen.two_space)))
            .into(binding.album)
        binding.trackName.text = savedTrack?.trackName
        binding.artistName.text = savedTrack?.artistName
        binding.trackTime.text = savedTrack?.trackTime
        binding.collection.text = savedTrack?.collectionName
        binding.collectionGroup.isVisible = savedTrack?.collectionName?.isNotEmpty() == true
        binding.year.text = savedTrack?.releaseDate?.take(FIRST_FOUR_CHARACTERS)
        binding.genre.text = savedTrack?.primaryGenreName
        binding.country.text = savedTrack?.country
    }

    private fun displayPlayer(mediaPlayerState: MediaPlayerState) {
        when (mediaPlayerState) {
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

    companion object {
        const val FIRST_FOUR_CHARACTERS = 4
    }

}