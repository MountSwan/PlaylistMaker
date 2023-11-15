package com.practicum.playlistmaker.data.mediaplayer

import android.media.MediaPlayer
import android.os.Handler
import com.practicum.playlistmaker.domain.CreateUpdateTimerTask
import com.practicum.playlistmaker.domain.usecases.SetTVTimePlayTrackTextUseCase
import java.text.SimpleDateFormat
import java.util.Locale

const val REFRESH_TIMER_MILLIS = 500L

class CreateUpdateTimerTaskImpl(
    private val setTVTimePlayTrackText: SetTVTimePlayTrackTextUseCase,
    private val mediaPlayer: MediaPlayer,
    private val mainThreadHandler: Handler
) : CreateUpdateTimerTask {

    override fun execute(): Runnable {
        return object : Runnable {
            override fun run() {
                val text = SimpleDateFormat(
                    "mm:ss",
                    Locale.getDefault()
                ).format(mediaPlayer.currentPosition)
                setTVTimePlayTrackText.execute(text)
                mainThreadHandler.postDelayed(this, REFRESH_TIMER_MILLIS)
            }
        }
    }

}