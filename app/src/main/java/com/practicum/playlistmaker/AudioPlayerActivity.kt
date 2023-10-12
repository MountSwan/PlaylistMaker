package com.practicum.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.practicum.playlistmaker.databinding.ActivityAudioplayerBinding
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAudioplayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioplayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.arrowBackImage.setOnClickListener {
            finish()
        }

        val sharedPrefs = getSharedPreferences(PRACTICUM_EXAMPLE_PREFERENCES, MODE_PRIVATE)
        val savedTrackInSharedPrefs = sharedPrefs.getString(SAVE_TRACK_FOR_AUDIO_PLAYER_KEY, null)
        val savedTrack =
            Gson().fromJson(savedTrackInSharedPrefs, Track::class.java)

        Glide.with(applicationContext)
            .load(savedTrack.artworkUrl100.replaceAfterLast('/',"512x512bb.jpg"))
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(applicationContext.resources.getDimensionPixelSize(R.dimen.two_space)))
            .into(binding.album)
        binding.trackName.text = savedTrack.trackName
        binding.artistName.text = savedTrack.artistName
        binding.trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(savedTrack.trackTimeMillis)
        binding.collection.text = savedTrack.collectionName
        binding.collectionGroup.isVisible = savedTrack.collectionName.isNotEmpty()
        binding.year.text = savedTrack.releaseDate.take(4)
        binding.genre.text = savedTrack.primaryGenreName
        binding.country.text = savedTrack.country
    }
}