package com.practicum.playlistmaker.domain


interface AudioPlayer {

    fun preparePlayer(urlForPlaying: String?)

    fun startPlayer()

    fun pausePlayer()

    fun releasePlayer()

    fun addListener(listener: MediaPlayerListener)

}