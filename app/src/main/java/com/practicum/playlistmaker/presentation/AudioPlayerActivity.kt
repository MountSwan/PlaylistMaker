package com.practicum.playlistmaker.presentation

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.SAVE_TRACK_FOR_AUDIO_PLAYER_KEY
import com.practicum.playlistmaker.databinding.ActivityAudioplayerBinding
import com.practicum.playlistmaker.domain.models.MediaPlayerState
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.usecases.GetMediaPlayerCurrentPositionUseCase
import com.practicum.playlistmaker.domain.usecases.GetMediaPlayerStateUseCase
import com.practicum.playlistmaker.domain.usecases.PauseMediaPlayerUseCase
import com.practicum.playlistmaker.domain.usecases.PrepareMediaPlayerUseCase
import com.practicum.playlistmaker.domain.usecases.ReleaseMediaPlayerUseCase
import com.practicum.playlistmaker.domain.usecases.StartMediaPlayerUseCase
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    private val audioPlayer =
        Creator.provideAudioPlayer()

    private val mainThreadHandler = Handler(Looper.getMainLooper())
    private val timerRunnable = createUpdateTimerTask()

    private val preparePlayer = PrepareMediaPlayerUseCase(audioPlayer)
    private val pausePlayer = PauseMediaPlayerUseCase(audioPlayer)
    private val startPlayer = StartMediaPlayerUseCase(audioPlayer)
    private val releasePlayer = ReleaseMediaPlayerUseCase(audioPlayer)
    private val getPlayerCurrentPosition = GetMediaPlayerCurrentPositionUseCase(audioPlayer)
    private val getPlayerState = GetMediaPlayerStateUseCase(audioPlayer)

    private lateinit var binding: ActivityAudioplayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioplayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.arrowBackImage.setOnClickListener {
            finish()
        }

        val savedTrack = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(SAVE_TRACK_FOR_AUDIO_PLAYER_KEY, Track::class.java)
        } else {
            intent.getParcelableExtra(SAVE_TRACK_FOR_AUDIO_PLAYER_KEY)
        }

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


        audioPlayer.addListener(
            MediaPlayerListenerImpl(
                audioPlayer, binding.controlPlay, binding.timePlayTrack,
                mainThreadHandler,
                timerRunnable
            )
        )

        preparePlayer.execute(savedTrack?.previewUrl)

        binding.controlPlay.setOnClickListener {
            playbackControl()
        }

    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainThreadHandler.removeCallbacks(timerRunnable)
        releasePlayer.execute()
    }

    private fun createUpdateTimerTask(): Runnable {
        return object : Runnable {
            override fun run() {
                binding.timePlayTrack.text =
                    SimpleDateFormat(
                        "mm:ss",
                        Locale.getDefault()
                    ).format(getPlayerCurrentPosition.execute())
                mainThreadHandler.postDelayed(this, REFRESH_TIMER_MILLIS)
            }
        }
    }

    private fun playbackControl() {
        when (getPlayerState.execute()) {
            is MediaPlayerState.Playing -> {
                pausePlayer()
            }

            is MediaPlayerState.Prepared, MediaPlayerState.Paused -> {
                startPlayer()
            }

            is MediaPlayerState.Default -> {}
        }
    }

    private fun startPlayer() {
        startPlayer.execute()
        binding.controlPlay.setImageResource(R.drawable.control_pause)
        mainThreadHandler.post(timerRunnable)
    }

    private fun pausePlayer() {
        pausePlayer.execute()
        binding.controlPlay.setImageResource(R.drawable.control_play)
        mainThreadHandler.removeCallbacks(timerRunnable)
    }

    companion object {
        const val FIRST_FOUR_CHARACTERS = 4
        const val REFRESH_TIMER_MILLIS = 500L
    }

}