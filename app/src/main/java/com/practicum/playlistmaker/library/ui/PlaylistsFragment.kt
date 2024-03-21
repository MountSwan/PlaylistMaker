package com.practicum.playlistmaker.library.ui

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.player.ui.AudioPlayerFragment
import com.practicum.playlistmaker.search.ui.SearchFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class PlaylistsFragment : Fragment() {

    companion object {

        fun newInstance() =
            PlaylistsFragment().apply {
                arguments = Bundle().apply {

                }
            }

        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private val adapter = PlaylistAdapter {
        if (clickDebounce()) {
            startPlaylistInfo(it.playlistId)
        }
    }

    private var isClickAllowed = true

    private val playlistsViewModel: PlaylistsViewModel by viewModel()

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlistsViewModel.observePlaylists().observe(viewLifecycleOwner) {
            adapter.playlists.clear()
            if (it.size == 0) {
                binding.placeholderImage.isVisible = true
                binding.placeholderMessage.isVisible = true
            } else {
                binding.placeholderImage.isVisible = false
                binding.placeholderMessage.isVisible = false
                adapter.playlists = mapPlaylistToPlaylistUI(it)
            }
            adapter.notifyDataSetChanged()
        }

        binding.recyclerView.layoutManager = GridLayoutManager(requireActivity(), 2)
        binding.recyclerView.adapter = adapter

        playlistsViewModel.getPlaylistsFromDatabase()

        binding.newPlaylistButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_libraryFragment_to_newPlaylistFragment,
                NewPlaylistFragment.createArgs(0)
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

    private fun startPlaylistInfo(playlistId: Int) {
        findNavController().navigate(
            R.id.action_libraryFragment_to_playlistInfoFragment,
            PlaylistInfoFragment.createArgs(playlistId)
        )
    }

}