package com.practicum.playlistmaker.library.ui

import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistInfoBinding
import com.practicum.playlistmaker.library.domain.models.EndCase
import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.player.ui.AudioPlayerFragment
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.models.TrackUi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistInfoFragment : Fragment() {

    companion object {
        fun newInstance() = PlaylistInfoFragment()

        const val ARGS_PLAYLIST_ID = "playlistId"
        fun createArgs(playlistId: Int): Bundle =
            bundleOf(ARGS_PLAYLIST_ID to playlistId)

        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private val adapter = TrackInPlaylistAdapter(
        clickListener = {
            if (clickDebounce()) {
                startAudioPlayer(it)
            }
        },
        onLongClickListener = {
            deleteTrackFromPlaylist(it.trackId)
        }
    )

    private var isClickAllowed = true
    private var playlistName = ""
    private var playlistId = 0

    private val viewModel by viewModel<PlaylistInfoViewModel> {
        parametersOf(getPlaylistId())
    }

    private var _binding: FragmentPlaylistInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tracksBottomSheetBehavior = BottomSheetBehavior.from(binding.tracksBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }

        val menuBottomSheetBehavior = BottomSheetBehavior.from(binding.menuBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        menuBottomSheetBehavior.addBottomSheetCallback(object :
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

        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = adapter

        viewModel.observePlaylistInfo().observe(viewLifecycleOwner) {
            drawScreen(it)
            drawMenuBottomSheet(it)
            playlistName = it.playlistName
        }

        viewModel.observeTracksInPlaylist().observe(viewLifecycleOwner) {
            addDataInScreen(it)
            drawTracksBottomSheet(it)
        }

        viewModel.observeSharePossibility().observe(viewLifecycleOwner) {
            if (it) {
                viewModel.sharePlaylist()
            } else {
                Toast.makeText(
                    requireContext(),
                    "${getString(R.string.share_is_not_possible)}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        viewModel.observeCloseFragment().observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigateUp()
            }
        }

        viewModel.getPlaylistInfoFromDatabase()

        binding.arrowBackImage.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.iconShare.setOnClickListener {
            viewModel.checkSharePossibility()
        }

        binding.iconMore.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }

        binding.menuSharePlaylist.setOnClickListener {
            viewModel.checkSharePossibility()
        }

        binding.menuDeletePlaylist.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            context?.let { it1 ->
                MaterialAlertDialogBuilder(it1)
                    .setTitle("${getString(R.string.request_delete_playlist)} «$playlistName»?")
                    .setNeutralButton(getString(R.string.no)) { dialog, which ->
                    }
                    .setPositiveButton(getString(R.string.yes)) { dialog, which ->
                        viewModel.deletePlaylist()
                    }
                    .show()
            }
        }

        binding.menuEditPlaylistInfo.setOnClickListener {
            findNavController().navigate(
                R.id.action_playlistInfoFragment_to_newPlaylistFragment,
                NewPlaylistFragment.createArgs(playlistId)
            )
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
            R.id.action_playlistInfoFragment_to_audioPlayerFragment,
            AudioPlayerFragment.createArgs(trackUi)
        )
    }

    private fun getPlaylistId(): Int {
        playlistId = requireArguments().getInt(ARGS_PLAYLIST_ID)
        return playlistId
    }

    private fun deleteTrackFromPlaylist(trackId: Long?) {
        context?.let { it1 ->
            MaterialAlertDialogBuilder(it1)
                .setTitle(getString(R.string.delete_track))
                .setMessage(getString(R.string.confirmation_delete_track))
                .setNeutralButton(getString(R.string.cancel)) { dialog, which ->
                }
                .setPositiveButton(getString(R.string.delete)) { dialog, which ->
                    viewModel.deleteTrackFromPlaylist(trackId)
                }
                .show()
        }
    }

    private fun drawScreen(playlist: Playlist) {
        Glide.with(requireContext())
            .load(getCoverImage(playlist.playlistName))
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(requireContext().resources.getDimensionPixelSize(R.dimen.two_space)))
            .into(binding.cover)
        binding.playlistName.text = playlist.playlistName
        binding.playlistDescription.text = playlist.playlistDescription
    }

    private fun drawMenuBottomSheet(playlist: Playlist) {
        Glide.with(requireContext())
            .load(getCoverImage(playlist.playlistName))
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(requireContext().resources.getDimensionPixelSize(R.dimen.half_space)))
            .into(binding.menuCover)
        binding.menuPlaylistName.text = playlist.playlistName
        val tracksNumberEnd = when(defineEnd(playlist.numberOfTracksInPlaylist)) {
            EndCase.NominativeSingle -> getString(R.string.track)
            EndCase.NominativeMultiple -> getString(R.string.of_track)
            EndCase.Genitive -> getString(R.string.of_tracks)
        }
        binding.menuTracksNumber.text = "${playlist.numberOfTracksInPlaylist} $tracksNumberEnd"
    }

    private fun getCoverImage(playlistName: String): File {
        val filePath =
            File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
        return File(filePath, "${playlistName}_cover.jpg")
    }

    private fun addDataInScreen(tracks: ArrayList<Track>) {
        var trackNumber = 0
        var playlistDurationMillis = 0L
        while (trackNumber < tracks.size) {
            playlistDurationMillis += tracks[trackNumber].trackTimeMillis!!
            trackNumber += 1
        }
        val playlistDuration = SimpleDateFormat("mm", Locale.getDefault()).format(playlistDurationMillis).toInt()
        val playlistDurationEnd = when(defineEnd(playlistDuration)) {
            EndCase.NominativeSingle -> getString(R.string.minute)
            EndCase.NominativeMultiple -> getString(R.string.minutes)
            EndCase.Genitive -> getString(R.string.of_minutes)
        }
        val tracksNumberEnd = when(defineEnd(tracks.size)) {
            EndCase.NominativeSingle -> getString(R.string.track)
            EndCase.NominativeMultiple -> getString(R.string.of_track)
            EndCase.Genitive -> getString(R.string.of_tracks)
        }
        binding.playlistDuration.text = "$playlistDuration $playlistDurationEnd"
        binding.tracksNumber.text = "${tracks.size} $tracksNumberEnd"
    }

    private fun drawTracksBottomSheet (tracks: ArrayList<Track>) {
        adapter.tracks.clear()
        if (tracks.size == 0) {
            binding.placeholderImage.isVisible = true
            binding.placeholderMessage.isVisible = true
        } else {
            binding.placeholderImage.isVisible = false
            binding.placeholderMessage.isVisible = false
            adapter.tracks = tracks
        }
        adapter.notifyDataSetChanged()
    }

    private fun defineEnd(number: Int): EndCase {
        return if (number < 10) {
            when (number) {
                1 -> EndCase.NominativeSingle
                2, 3, 4 -> EndCase.NominativeMultiple
                else -> EndCase.Genitive
            }
        } else if (number.toString()[number.toString().length - 2].digitToInt() == 1) {
            EndCase.Genitive
        } else {
            when (number.toString().last().digitToInt()) {
                1 -> EndCase.NominativeSingle
                2, 3, 4 -> EndCase.NominativeMultiple
                else -> EndCase.Genitive
            }
        }

    }

}