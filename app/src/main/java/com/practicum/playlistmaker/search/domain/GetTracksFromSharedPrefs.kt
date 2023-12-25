package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.search.domain.models.Track

interface GetTracksFromSharedPrefs {
    fun execute(tracksInHistory: ArrayList<Track>)
}