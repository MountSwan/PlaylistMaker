package com.practicum.playlistmaker.search.data

import android.content.Context
import android.content.Intent
import com.practicum.playlistmaker.SAVE_TRACK_FOR_AUDIO_PLAYER_KEY
import com.practicum.playlistmaker.search.data.models.TrackDto
import com.practicum.playlistmaker.search.domain.StartAudioPlayer
import com.practicum.playlistmaker.search.domain.models.Track

class StartAudioPlayerImpl(private val context: Context, private val audioPlayerIntent: Intent) : StartAudioPlayer {

    override fun execute(track: Track) {

        val trackDto = TrackDto(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl
        )

        audioPlayerIntent.putExtra(SAVE_TRACK_FOR_AUDIO_PLAYER_KEY, trackDto)
        audioPlayerIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(audioPlayerIntent)
    }
}