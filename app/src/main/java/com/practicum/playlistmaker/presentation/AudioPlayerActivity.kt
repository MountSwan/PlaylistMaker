package com.practicum.playlistmaker.presentation

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.SAVE_TRACK_FOR_AUDIO_PLAYER_KEY
import com.practicum.playlistmaker.data.MediaPlayerFromData
import com.practicum.playlistmaker.databinding.ActivityAudioplayerBinding
import com.practicum.playlistmaker.domain.models.MediaPlayerState
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.usecases.CreateUpdateTimerTaskUseCase
import com.practicum.playlistmaker.domain.usecases.PauseMediaPlayerUseCase
import com.practicum.playlistmaker.domain.usecases.PlaybackControlUseCase
import com.practicum.playlistmaker.domain.usecases.PrepareMediaPlayerUseCase
import com.practicum.playlistmaker.domain.usecases.ReleaseMediaPlayerUseCase
import com.practicum.playlistmaker.data.mediaplayer.CreateUpdateTimerTaskImpl
import com.practicum.playlistmaker.data.mediaplayer.PauseMediaPlayerImpl
import com.practicum.playlistmaker.domain.usecases.EnableIVControlPlayUseCase
import com.practicum.playlistmaker.domain.usecases.SetTVTimePlayTrackTextUseCase
import com.practicum.playlistmaker.presentation.mediaplayer.EnableIVControlPlayImpl
import com.practicum.playlistmaker.data.mediaplayer.PrepareMediaPlayerImpl
import com.practicum.playlistmaker.data.mediaplayer.ReleaseMediaPlayerImpl
import com.practicum.playlistmaker.presentation.mediaplayer.SetIVControlPlayImpl
import com.practicum.playlistmaker.presentation.mediaplayer.SetTVTimePlayTrackTextImpl
import com.practicum.playlistmaker.data.mediaplayer.StartMediaPlayerImpl
import com.practicum.playlistmaker.domain.models.Parameters

class AudioPlayerActivity : AppCompatActivity() {

    private val mediaPlayerFromData = MediaPlayerFromData()
    var mediaPlayer = mediaPlayerFromData.getMediaPlayer

    lateinit var parameters: Parameters

    private val mediaPlayerState = MediaPlayerState()

    private val mainThreadHandler = Handler(Looper.getMainLooper())

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

        val setTVTimePlayTrackText = SetTVTimePlayTrackTextUseCase(
            SetTVTimePlayTrackTextImpl(
                tvTimePlayTrack = binding.timePlayTrack
            )
        )

        val enableIVControlPlay = EnableIVControlPlayUseCase(
            EnableIVControlPlayImpl(
                ivControlPlay = binding.controlPlay
            )
        )

        val timerRunnable = CreateUpdateTimerTaskUseCase(
            CreateUpdateTimerTaskImpl(
                setTVTimePlayTrackText = setTVTimePlayTrackText,
                mediaPlayer = mediaPlayer,
                mainThreadHandler = mainThreadHandler
            )
        ).execute()

        parameters = Parameters(
            urlForPlaying = savedTrack?.previewUrl,
            mediaPlayerState = mediaPlayerState,
            setTVTimePlayTrackText = setTVTimePlayTrackText,
            enableIVControlPlay = enableIVControlPlay,
            setIVControlPlay = SetIVControlPlayImpl(ivControlPlay = binding.controlPlay),
            imageCodePlayForIVControlPlay = R.drawable.control_play,
            imageCodePauseForIVControlPlay = R.drawable.control_pause,
            mainThreadHandler = mainThreadHandler,
            timerRunnable = timerRunnable
        )

        val prepareMediaPlayer =
            PrepareMediaPlayerUseCase(
                PrepareMediaPlayerImpl(
                    mediaPlayer = mediaPlayer,
                    params = parameters
                )
            )
        prepareMediaPlayer.execute()

        binding.controlPlay.setOnClickListener {
            val playbackControl = PlaybackControlUseCase(
                PauseMediaPlayerImpl(
                    mediaPlayer = mediaPlayer,
                    params = parameters
                ), StartMediaPlayerImpl(
                    mediaPlayer = mediaPlayer,
                    params = parameters
                )
            )
            playbackControl.execute(mediaPlayerState = mediaPlayerState)
        }

    }

    override fun onPause() {
        super.onPause()
        val pausePlayer = PauseMediaPlayerUseCase(
            PauseMediaPlayerImpl(
                mediaPlayer = mediaPlayer,
                params = parameters
            )
        )
        pausePlayer.execute()
    }

    override fun onDestroy() {
        super.onDestroy()
        val releaseMediaPlayer = ReleaseMediaPlayerUseCase(
            ReleaseMediaPlayerImpl(
                mediaPlayer = mediaPlayer,
                params = parameters
            )
        )
        releaseMediaPlayer.execute()
    }


    companion object {
        const val FIRST_FOUR_CHARACTERS = 4
    }

}