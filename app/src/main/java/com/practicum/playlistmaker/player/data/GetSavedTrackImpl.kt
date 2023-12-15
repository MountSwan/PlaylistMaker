package com.practicum.playlistmaker.player.data

import android.content.Intent
import android.os.Build
import com.practicum.playlistmaker.SAVE_TRACK_FOR_AUDIO_PLAYER_KEY
import com.practicum.playlistmaker.player.domain.GetSavedTrack
import com.practicum.playlistmaker.search.data.models.TrackDto
import com.practicum.playlistmaker.search.domain.models.Track

class GetSavedTrackImpl(private val intent: Intent) : GetSavedTrack {
    override fun execute(): Track? {
        val savedTrackDto = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(SAVE_TRACK_FOR_AUDIO_PLAYER_KEY, TrackDto::class.java)
        } else {
            intent.getParcelableExtra(SAVE_TRACK_FOR_AUDIO_PLAYER_KEY)
        }
        val savedTrack = savedTrackDto?.let {
            Track(
                trackId = it.trackId,
                trackName = it.trackName,
                artistName = it.artistName,
                trackTimeMillis = it.trackTimeMillis,
                trackTime = it.trackTime,
                artworkUrl100 = it.artworkUrl100,
                artworkUrl512 = it.artworkUrl512,
                collectionName = it.collectionName,
                releaseDate = it.releaseDate,
                primaryGenreName = it.primaryGenreName,
                country = it.country,
                previewUrl = it.previewUrl
            )
        }
        return savedTrack
    }
}