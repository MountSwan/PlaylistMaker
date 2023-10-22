package com.practicum.playlistmaker

import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.databinding.ActivityAudioplayerBinding
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val REFRESH_TIMER = 500L
    }

    private var playerState = STATE_DEFAULT
    private var mediaPlayer = MediaPlayer()
    private val mainThreadHandler = Handler(Looper.getMainLooper())
    var startTime = 0L
    private var endTime = 0L
    private val timerRunnable = createUpdateTimerTask()

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
        binding.year.text = savedTrack?.releaseDate?.take(4)
        binding.genre.text = savedTrack?.primaryGenreName
        binding.country.text = savedTrack?.country

        preparePlayer(savedTrack?.previewUrl)

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
        mediaPlayer.release()
    }

    private fun preparePlayer(urlForPlaying: String?) {
        mediaPlayer.setDataSource(urlForPlaying)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            binding.controlPlay.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            binding.controlPlay.setImageResource(R.drawable.control_play)
            playerState = STATE_PREPARED
            mainThreadHandler.removeCallbacks(timerRunnable)
            binding.timePlayTrack.text = getString(R.string.time_play_track)
            startTime = 0L
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        binding.controlPlay.setImageResource(R.drawable.control_pause)
        playerState = STATE_PLAYING
        if (startTime == 0L) {
            startTime = System.currentTimeMillis()
        } else {
            startTime += System.currentTimeMillis() - endTime
        }
        mainThreadHandler.post(timerRunnable)
    }

    private fun createUpdateTimerTask(): Runnable {
        return object : Runnable {
            override fun run() {
                val elapsedTime = System.currentTimeMillis() - startTime
                binding.timePlayTrack.text =
                    SimpleDateFormat("mm:ss", Locale.getDefault()).format(elapsedTime)
                mainThreadHandler.postDelayed(this, REFRESH_TIMER)
            }
        }
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        binding.controlPlay.setImageResource(R.drawable.control_play)
        playerState = STATE_PAUSED
        mainThreadHandler.removeCallbacks(timerRunnable)
        endTime = System.currentTimeMillis()
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

}