package com.practicum.playlistmaker.domain.models

import android.os.Handler
import com.practicum.playlistmaker.domain.SetIVControlPlay
import com.practicum.playlistmaker.domain.usecases.EnableIVControlPlayUseCase
import com.practicum.playlistmaker.domain.usecases.SetTVTimePlayTrackTextUseCase

data class Parameters(
    val urlForPlaying: String?,
    val mediaPlayerState: MediaPlayerState,
    val setTVTimePlayTrackText: SetTVTimePlayTrackTextUseCase,
    val enableIVControlPlay: EnableIVControlPlayUseCase,
    val setIVControlPlay: SetIVControlPlay,
    val imageCodePlayForIVControlPlay: Int,
    val imageCodePauseForIVControlPlay: Int,
    val mainThreadHandler: Handler,
    val timerRunnable: Runnable
)
