package com.practicum.playlistmaker.search.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.SAVE_TRACK_FOR_AUDIO_PLAYER_KEY
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.player.ui.AudioPlayerActivity
import com.practicum.playlistmaker.search.ui.models.TrackUi
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    companion object {
        const val SEARCH_REQUEST = "SEARCH_REQUEST"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private var text: String = ""

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

    private lateinit var binding: ActivitySearchBinding
    private var searchRequest: Editable? = null
    private var inputMethodManager: InputMethodManager? = null
    private var isClickAllowed = true

    private val mainThreadHandler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable {
        inputMethodManager?.hideSoftInputFromWindow(binding.inputEditText.windowToken, 0)
        search(searchRequest)
    }

    private val viewModel by viewModel<TracksSearchViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager

        viewModel.observeTracks().observe(this) {
            adapter.tracks = it
        }

        viewModel.getTracksInHistoryFromSharedPrefs()
        viewModel.observeTracksInHistory().observe(this) {
            adapterHistory.tracks = it
            binding.inputEditText.setOnFocusChangeListener { view, hasFocus ->
                binding.historyOfSearch.isVisible =
                    hasFocus && binding.inputEditText.text.isEmpty() && it.size > 0
            }
        }

        viewModel.observeShowMessage().observe(this) {
            if (it.text.isNotEmpty()) {
                binding.placeholderMessage.isVisible = true
                binding.placeholderImage.isVisible = true
                adapter.notifyDataSetChanged()
                binding.placeholderMessage.text = it.text
                binding.placeholderImage.setImageResource(it.image)
            } else {
                binding.placeholderMessage.isVisible = false
                binding.placeholderImage.isVisible = false
            }
        }

        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = adapter

        binding.recyclerViewOfHistory.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewOfHistory.adapter = adapterHistory

        binding.arrowBackImage.setOnClickListener {
            finish()
        }

        binding.clearIcon.setOnClickListener {
            binding.inputEditText.setText("")
            inputMethodManager?.hideSoftInputFromWindow(binding.inputEditText.windowToken, 0)
            hideAllExceptHistory()
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
                viewModel.doOnTextChange(textSearch)
                viewModel.observeSearchState().observe(this@SearchActivity) {
                    binding.clearIcon.isVisible = it.clearButtonVisibility
                    if (binding.inputEditText.hasFocus() && textSearch?.isEmpty() == true && it.tracksInHistorySize > 0) {
                        hideAllExceptHistory()
                        binding.historyOfSearch.isVisible = true
                    } else {
                        binding.historyOfSearch.isVisible = false
                    }
                }
                text = textSearch.toString()
                searchDebounce()
            }

            override fun afterTextChanged(textSearch: Editable?) {
                // empty
            }
        }
        binding.inputEditText.addTextChangedListener(simpleTextWatcher)

        binding.inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                mainThreadHandler.removeCallbacks(searchRunnable)
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
            binding.historyOfSearch.isVisible = false
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_REQUEST, text)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        text = savedInstanceState.getString(SEARCH_REQUEST, "")
        binding.inputEditText.setText(text)
    }

    private fun search(searchRequest: Editable?) {
        if (searchRequest?.isNotEmpty() == true) {
            viewModel.searchTracks(
                searchRequest = searchRequest.toString(),
                nothingFoundMessage = getString(R.string.nothing_found),
                somethingWentWrongMessage = getString(R.string.something_went_wrong)
            )
            viewModel.observeSearchState().observe(this) {
                binding.refreshButton.isVisible = it.refreshButtonIsVisible
                binding.recyclerView.isVisible = it.recyclerViewIsVisible
                binding.progressBar.isVisible = it.progressBarIsVisible
                if (it.adapterNotifyDataSetChanged) {
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun addInHistory(track: Track) {
        viewModel.addInHistory(track)
        adapterHistory.notifyDataSetChanged()
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
            previewUrl = track.previewUrl
        )
        val audioPlayerIntent = Intent(this, AudioPlayerActivity::class.java).apply {
            putExtra(SAVE_TRACK_FOR_AUDIO_PLAYER_KEY, trackUi)
        }
        startActivity(audioPlayerIntent)
    }

    private fun hideAllExceptHistory() {
        viewModel.tracksClear()
        adapter.notifyDataSetChanged()
        binding.placeholderMessage.isVisible = false
        binding.placeholderImage.isVisible = false
        binding.refreshButton.isVisible = false
    }

    private fun searchDebounce() {
        searchRequest = binding.inputEditText.text
        mainThreadHandler.removeCallbacks(searchRunnable)
        mainThreadHandler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            mainThreadHandler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

}