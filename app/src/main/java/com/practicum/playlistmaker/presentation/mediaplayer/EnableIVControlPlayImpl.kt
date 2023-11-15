package com.practicum.playlistmaker.presentation.mediaplayer

import android.widget.ImageView
import com.practicum.playlistmaker.domain.EnableIVControlPlay

class EnableIVControlPlayImpl(private val ivControlPlay: ImageView): EnableIVControlPlay {
    override fun execute() {
        ivControlPlay.isEnabled = true
    }
}