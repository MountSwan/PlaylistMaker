package com.practicum.playlistmaker.presentation.mediaplayer

import android.widget.ImageView
import com.practicum.playlistmaker.domain.SetIVControlPlay

class SetIVControlPlayImpl(private val ivControlPlay: ImageView): SetIVControlPlay {
    override fun execute(imageCode: Int) {
        ivControlPlay.setImageResource(imageCode)
    }
}