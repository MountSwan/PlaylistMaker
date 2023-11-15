package com.practicum.playlistmaker.presentation.mediaplayer

import android.widget.TextView
import com.practicum.playlistmaker.domain.SetTVTimePlayTrackText

class SetTVTimePlayTrackTextImpl(
    private val tvTimePlayTrack: TextView
) : SetTVTimePlayTrackText {
    override fun execute(text: String) {
        tvTimePlayTrack.text = text
    }

}