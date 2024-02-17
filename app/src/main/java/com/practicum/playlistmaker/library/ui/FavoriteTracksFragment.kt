package com.practicum.playlistmaker.library.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.SAVE_TRACK_FOR_AUDIO_PLAYER_KEY
import com.practicum.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.practicum.playlistmaker.player.ui.AudioPlayerActivity
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.TrackAdapter
import com.practicum.playlistmaker.search.ui.models.TrackUi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : Fragment() {

    companion object {

        fun newInstance() =
            FavoriteTracksFragment().apply {
                arguments = Bundle().apply {

                }
            }

        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private val adapter = TrackAdapter {
        if (clickDebounce()) {
            startAudioPlayer(it)
        }
    }

    private var isClickAllowed = true

    private var onPause: Boolean = false

    private val favoriteTracksViewModel: FavoriteTracksViewModel by viewModel()

    private var _binding: FragmentFavoriteTracksBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onPause = false

        favoriteTracksViewModel.observeFavoriteTrackState().observe(viewLifecycleOwner) {
            when (it) {
                is FavoriteTracksState.Empty -> {
                    binding.placeholderImage.isVisible = true
                    binding.placeholderMessage.isVisible = true
                    adapter.tracks.clear()
                    adapter.notifyDataSetChanged()
                }

                is FavoriteTracksState.Content -> {
                    binding.placeholderImage.isVisible = false
                    binding.placeholderMessage.isVisible = false
                    adapter.tracks = it.tracks
                    adapter.notifyDataSetChanged()
                }
            }
        }

        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = adapter

        favoriteTracksViewModel.getFavoriteTracks()

    }

    override fun onPause() {
        super.onPause()
        onPause = true
    }

    override fun onStart() {
        super.onStart()
        if (onPause) {
            favoriteTracksViewModel.getFavoriteTracks()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    private fun startAudioPlayer(track: Track) {
        lifecycleScope.launch {
            val trackUi = TrackUi(
                trackId = track.trackId,
                trackName = track.trackName,
                artistName = track.artistName,
                trackTimeMillis = track.trackTimeMillis,
                trackTime = track.trackTime,
                artworkUrl100 = track.artworkUrl100,
                artworkUrl512 = track.artworkUrl512,
                collectionName = track.collectionName,
                releaseDate = track.releaseDate,
                primaryGenreName = track.primaryGenreName,
                country = track.country,
                previewUrl = track.previewUrl,
                isFavorite = favoriteTracksViewModel.checkIsFavorite(track.trackId)
            )
            val audioPlayerIntent =
                Intent(requireContext(), AudioPlayerActivity::class.java).apply {
                    putExtra(SAVE_TRACK_FOR_AUDIO_PLAYER_KEY, trackUi)
                }
            startActivity(audioPlayerIntent)
        }

    }

}