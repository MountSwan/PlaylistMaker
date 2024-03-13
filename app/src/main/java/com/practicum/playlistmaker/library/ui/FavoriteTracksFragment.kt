package com.practicum.playlistmaker.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.practicum.playlistmaker.player.ui.AudioPlayerFragment
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
            isFavorite = track.isFavorite,
        )

        findNavController().navigate(
            R.id.action_libraryFragment_to_audioPlayerFragment,
            AudioPlayerFragment.createArgs(trackUi)
        )
    }

}