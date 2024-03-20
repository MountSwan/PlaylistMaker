package com.practicum.playlistmaker.search.ui

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.player.ui.AudioPlayerFragment
import com.practicum.playlistmaker.search.domain.models.DisplayState
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.models.TrackUi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private var text: String = ""

    private var searchJob: Job? = null

    private var onPause: Boolean = false

    private val adapter = TrackAdapter {
        if (clickDebounce()) {
            addInHistory(it)
            startAudioPlayer(it)
        }
    }
    private val adapterHistory = TrackAdapter {
        if (clickDebounce()) {
            addInHistory(it)
            startAudioPlayer(it)
        }
    }

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private var searchRequest: Editable? = null

    private var inputMethodManager: InputMethodManager? = null
    private var isClickAllowed = true

    private val viewModel: TracksSearchViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onPause = false

        viewModel.getTracksInHistoryFromSharedPrefs()

        inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager

        viewModel.observeTracks().observe(viewLifecycleOwner) {
            adapter.tracks = it
            adapter.notifyDataSetChanged()
        }

        viewModel.observeTracksInHistory().observe(viewLifecycleOwner) {
            adapterHistory.tracks = it
            adapterHistory.notifyDataSetChanged()
            if (binding.inputEditText.text.isEmpty() && it.size > 0) {
                viewModel.setDisplayState(DisplayState.SearchHistory)
            } else if (binding.refreshButton.isVisible) {
                viewModel.setDisplayState(DisplayState.PlaceholdersWithRefreshButton)
            } else if (binding.placeholderImage.isVisible) {
                viewModel.setDisplayState(DisplayState.PlaceholdersWithoutRefreshButton)
            } else {
                viewModel.setDisplayState(DisplayState.TracksSearch)
            }
        }

        viewModel.observeSearchState().observe(viewLifecycleOwner) {
            binding.clearIcon.isVisible = it.clearButtonVisibility
            binding.refreshButton.isVisible = it.refreshButtonIsVisible
            binding.recyclerView.isVisible = it.recyclerViewIsVisible
            binding.progressBar.isVisible = it.progressBarIsVisible
            if (text.isEmpty() && it.tracksInHistorySize > 0) {
                viewModel.tracksClear()
                viewModel.setDisplayState(DisplayState.SearchHistory)
            } else {
                if (binding.refreshButton.isVisible) {
                    viewModel.setDisplayState(DisplayState.PlaceholdersWithRefreshButton)
                } else if (binding.placeholderImage.isVisible) {
                    viewModel.setDisplayState(DisplayState.PlaceholdersWithoutRefreshButton)
                } else {
                    viewModel.setDisplayState(DisplayState.TracksSearch)
                }
            }
        }

        viewModel.observeShowMessage().observe(viewLifecycleOwner) {
            if (it.text.isNotEmpty()) {
                if (binding.refreshButton.isVisible) {
                    viewModel.setDisplayState(DisplayState.PlaceholdersWithRefreshButton)
                } else {
                    viewModel.setDisplayState(DisplayState.PlaceholdersWithoutRefreshButton)
                }
                binding.placeholderMessage.text = it.text
                binding.placeholderImage.setImageResource(it.image)
            } else {
                if (binding.historyOfSearch.isVisible) {
                    viewModel.setDisplayState(DisplayState.SearchHistory)
                } else {
                    viewModel.setDisplayState(DisplayState.TracksSearch)
                }
            }
        }

        viewModel.observeDisplayState().observe(viewLifecycleOwner) {
            when (it) {
                DisplayState.TracksSearch -> {
                    binding.historyOfSearch.isVisible = false
                    binding.placeholderMessage.isVisible = false
                    binding.placeholderImage.isVisible = false
                    binding.refreshButton.isVisible = false
                }
                DisplayState.SearchHistory -> {
                    binding.historyOfSearch.isVisible = true
                    binding.placeholderMessage.isVisible = false
                    binding.placeholderImage.isVisible = false
                    binding.refreshButton.isVisible = false
                }
                DisplayState.PlaceholdersWithoutRefreshButton -> {
                    binding.historyOfSearch.isVisible = false
                    binding.placeholderMessage.isVisible = true
                    binding.placeholderImage.isVisible = true
                    binding.refreshButton.isVisible = false
                }
                DisplayState.PlaceholdersWithRefreshButton -> {
                    binding.historyOfSearch.isVisible = false
                    binding.placeholderMessage.isVisible = true
                    binding.placeholderImage.isVisible = true
                    binding.refreshButton.isVisible = true
                }
            }
        }

        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = adapter

        binding.recyclerViewOfHistory.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewOfHistory.adapter = adapterHistory

        binding.clearIcon.setOnClickListener {
            binding.inputEditText.setText("")
            inputMethodManager?.hideSoftInputFromWindow(binding.inputEditText.windowToken, 0)
            viewModel.tracksClear()
            binding.placeholderImage.isVisible = false
            binding.placeholderMessage.isVisible = false
            viewModel.hideRefreshButton()
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(
                textSearch: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                // empty
            }

            override fun onTextChanged(
                textSearch: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                text = textSearch.toString()
                viewModel.doOnTextChange(textSearch)
                searchDebounce()
            }

            override fun afterTextChanged(textSearch: Editable?) {
                // empty
            }
        }

        binding.inputEditText.addTextChangedListener(simpleTextWatcher)

        binding.inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchJob?.cancel()
                searchRequest = binding.inputEditText.text
                search(searchRequest)

                true
            }
            false
        }

        binding.refreshButton.setOnClickListener {
            search(searchRequest)
        }

        binding.clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
            viewModel.setDisplayState(DisplayState.TracksSearch)
        }

    }

    override fun onPause() {
        super.onPause()
        searchJob?.cancel()
        onPause = true
    }

    override fun onStart() {
        super.onStart()
        if (onPause) {
            viewModel.getTracksInHistoryFromSharedPrefs()
            searchDebounce()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun search(searchRequest: Editable?) {
        if (searchRequest?.isNotEmpty() == true) {
            viewModel.searchTracks(
                searchRequest = searchRequest.toString(),
                nothingFoundMessage = getString(R.string.nothing_found),
                somethingWentWrongMessage = getString(R.string.something_went_wrong)
            )
        }
    }

    private fun addInHistory(track: Track) {
        viewModel.addInHistory(track)
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
            R.id.action_searchFragment_to_audioPlayerFragment,
            AudioPlayerFragment.createArgs(trackUi)
        )
    }

    private fun searchDebounce() {
        searchRequest = binding.inputEditText.text
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            inputMethodManager?.hideSoftInputFromWindow(binding.inputEditText.windowToken, 0)
            search(searchRequest)
        }
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