package com.practicum.playlistmaker.player.domain

import com.practicum.playlistmaker.player.domain.models.MediaPlayerState

interface GetMediaPlayerState {
    fun execute(): MediaPlayerState
}