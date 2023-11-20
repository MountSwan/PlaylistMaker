package com.practicum.playlistmaker.domain

interface MediaPlayerListener {

    fun onCompletionMediaPlayer()

    fun onPreparedMediaPlayer()
}