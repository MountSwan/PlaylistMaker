package com.practicum.playlistmaker.player.ui

import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentAudioPlayerBinding
import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.library.ui.NewPlaylistFragment
import com.practicum.playlistmaker.library.ui.PlaylistUI
import com.practicum.playlistmaker.player.domain.models.MediaPlayerState
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.models.TrackUi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File


class AudioPlayerFragment : Fragment() {

    companion object {
        const val ARGS_TRACK = "track"
        fun createArgs(track: TrackUi): Bundle =
            bundleOf(ARGS_TRACK to track)

        const val FIRST_FOUR_CHARACTERS = 4
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private lateinit var playlist: PlaylistUI

    private val adapter = PlaylistAdapter {
        if (clickDebounce()) {
            playlist = it
            viewModel.addTrackInPlaylist(it)
        }
    }

    private val viewModel by viewModel<AudioPlayerViewModel> {
        parametersOf(getSavedTrack())
    }

    private var isClickAllowed = true

    private var _binding: FragmentAudioPlayerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAudioPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.standardBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.isVisible = false
                    }

                    else -> {
                        binding.overlay.isVisible = true
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        binding.arrowBackImage.setOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.startListenerPlayerStateJob()

        viewModel.observeSavedTrack().observe(viewLifecycleOwner) {
            drawScreen(it)
        }

        viewModel.observePlayerState().observe(viewLifecycleOwner) {
            displayPlayer(it)
        }

        viewModel.observeTimePlayTrack().observe(viewLifecycleOwner) {
            binding.timePlayTrack.text = it
        }

        viewModel.observePlaylistsFromDatabase()
            .observe(viewLifecycleOwner) { playlistsFromDatabase ->
                adapter.playlists.clear()
                if (playlistsFromDatabase.size == 0) {
                    binding.placeholderImage.isVisible = true
                    binding.placeholderMessage.isVisible = true
                } else {
                    binding.placeholderImage.isVisible = false
                    binding.placeholderMessage.isVisible = false
                    adapter.playlists = mapPlaylistToPlaylistUI(playlistsFromDatabase)
                }
                adapter.notifyDataSetChanged()
            }

        viewModel.observeResultOfAddingTrackInPlaylist().observe(viewLifecycleOwner) {
            if (it) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                Toast.makeText(
                    requireContext(),
                    "${getString(R.string.add_to_playlist_true)} ${playlist.playlistName}",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "${getString(R.string.add_to_playlist_false)} ${playlist.playlistName}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = adapter

        viewModel.preparePlayer()

        binding.controlPlay.setOnClickListener {
            viewModel.playbackControl()
        }

        binding.addToFavorites.setOnClickListener {
            viewModel.onFavoriteClicked()
        }

        binding.addToPlaylist.setOnClickListener {
            viewModel.getPlaylistsFromDatabase()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }

        binding.newPlaylistButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_audioPlayerFragment_to_newPlaylistFragment,
                NewPlaylistFragment.createArgs(0)
            )
        }
    }

    override fun onPause() {
        binding.controlPlay.setImageResource(R.drawable.control_play)
        viewModel.pausePlayer()
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.releasePlayer()
        _binding = null
    }

    private fun drawScreen(savedTrack: Track?) {
        Glide.with(requireContext())
            .load(savedTrack?.artworkUrl512)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(requireContext().resources.getDimensionPixelSize(R.dimen.two_space)))
            .into(binding.album)
        binding.trackName.text = savedTrack?.trackName
        binding.artistName.text = savedTrack?.artistName
        binding.trackTime.text = savedTrack?.trackTime
        binding.collection.text = savedTrack?.collectionName
        binding.collectionGroup.isVisible = savedTrack?.collectionName?.isNotEmpty() == true
        binding.year.text = savedTrack?.releaseDate?.take(FIRST_FOUR_CHARACTERS)
        binding.genre.text = savedTrack?.primaryGenreName
        binding.country.text = savedTrack?.country
        if (savedTrack?.isFavorite == true) {
            binding.addToFavorites.setImageResource(R.drawable.is_favorite)
        } else {
            binding.addToFavorites.setImageResource(R.drawable.is_not_favorite)
        }
    }

    private fun displayPlayer(mediaPlayerState: MediaPlayerState) {
        when (mediaPlayerState) {
            is MediaPlayerState.Prepared.OnPrepared -> {
                binding.controlPlay.isEnabled = true
                binding.timePlayTrack.text = "00:00"
                viewModel.defineMediaPlayerStatePreparedAsDefault()
            }

            is MediaPlayerState.Prepared.OnCompletion -> {
                binding.controlPlay.setImageResource(R.drawable.control_play)
                binding.timePlayTrack.text = "00:00"
                viewModel.defineMediaPlayerStatePreparedAsDefault()
            }

            is MediaPlayerState.Playing -> {
                binding.controlPlay.setImageResource(R.drawable.control_pause)
            }

            is MediaPlayerState.Paused -> {
                binding.controlPlay.setImageResource(R.drawable.control_play)
            }

            is MediaPlayerState.Prepared.Default, MediaPlayerState.Default -> Unit
        }
    }

    private fun getSavedTrack(): Track? {
        val savedTrackUi = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getParcelable(ARGS_TRACK, TrackUi::class.java)
        } else {
            requireArguments().getParcelable(ARGS_TRACK)
        }
        if (savedTrackUi == null) {
            findNavController().navigateUp()
        }
        return savedTrackUi?.let {
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
                previewUrl = it.previewUrl,
                isFavorite = it.isFavorite,
            )
        }
    }

    private fun mapPlaylistToPlaylistUI(playlists: ArrayList<Playlist>): ArrayList<PlaylistUI> {
        val playlistsUI = ArrayList<PlaylistUI>()
        playlistsUI.addAll(playlists.map {
            PlaylistUI(
                playlistId = it.playlistId,
                playlistName = it.playlistName,
                playlistDescription = it.playlistDescription,
                listOfTracksId = it.listOfTracksId,
                numberOfTracksInPlaylist = it.numberOfTracksInPlaylist,
                coverImage = getCoverImage(it.playlistName)
            )
        })
        return playlistsUI
    }

    private fun getCoverImage(playlistName: String): File {
        val filePath =
            File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
        return File(filePath, "${playlistName}_cover.jpg")
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

}