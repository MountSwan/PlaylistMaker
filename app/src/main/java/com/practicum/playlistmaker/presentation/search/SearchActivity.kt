package com.practicum.playlistmaker.presentation.search

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
import com.practicum.playlistmaker.data.network.ITunesApi
import com.practicum.playlistmaker.data.dto.ITunesResponse
import com.practicum.playlistmaker.PRACTICUM_EXAMPLE_PREFERENCES
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.SAVE_TRACK_FOR_AUDIO_PLAYER_KEY
import com.practicum.playlistmaker.data.SearchHistory
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.presentation.AudioPlayerActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    companion object {
        const val SEARCH_REQUEST = "SEARCH_REQUEST"
        const val EXECUTED_REQUEST = 200
        const val MAX_NUMBER_TRACKS_IN_SEARCH_HISTORY = 10
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private var text: String = ""
    private val iTunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(ITunesApi::class.java)

    private val tracks = ArrayList<Track>()
    private val tracksInHistory = ArrayList<Track>()
    private var searchHistory: SearchHistory? = null
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager

        adapter.tracks = tracks

        searchHistory =
            SearchHistory(getSharedPreferences(PRACTICUM_EXAMPLE_PREFERENCES, MODE_PRIVATE))
        searchHistory?.getTracksFromSharedPrefs(tracksInHistory)
        adapterHistory.tracks = tracksInHistory

        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = adapter

        binding.recyclerViewOfHistory.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewOfHistory.adapter = adapterHistory

        binding.inputEditText.setOnFocusChangeListener { view, hasFocus ->
            binding.historyOfSearch.isVisible =
                hasFocus && binding.inputEditText.text.isEmpty() && tracksInHistory.size > 0
        }

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
                binding.clearIcon.isVisible = clearButtonVisibility(textSearch)
                text = textSearch.toString()
                if (binding.inputEditText.hasFocus() && textSearch?.isEmpty() == true && tracksInHistory.size > 0) {
                    hideAllExceptHistory()
                    binding.historyOfSearch.isVisible = true
                } else {
                    binding.historyOfSearch.isVisible = false
                }
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
            searchHistory?.clearHistory(tracksInHistory)
            binding.historyOfSearch.isVisible = false
        }

    }

    private fun clearButtonVisibility(textSearch: CharSequence?): Boolean {
        return !textSearch.isNullOrEmpty()
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

    private fun showMessage(text: String, image: Int) {
        if (text.isNotEmpty()) {
            binding.placeholderMessage.isVisible = true
            binding.placeholderImage.isVisible = true
            tracks.clear()
            adapter.notifyDataSetChanged()
            binding.placeholderMessage.text = text
            binding.placeholderImage.setImageResource(image)
        } else {
            binding.placeholderMessage.isVisible = false
            binding.placeholderImage.isVisible = false
        }
    }

    private fun search(searchRequest: Editable?) {
        if (searchRequest?.isNotEmpty() == true) {
            binding.refreshButton.isVisible = false
            binding.recyclerView.isVisible = false
            binding.progressBar.isVisible = true
            iTunesService.search(searchRequest.toString()).enqueue(object :
                Callback<ITunesResponse> {
                override fun onResponse(
                    call: Call<ITunesResponse>,
                    response: Response<ITunesResponse>
                ) {
                    binding.progressBar.isVisible = false
                    if (response.code() == EXECUTED_REQUEST) {
                        tracks.clear()
                        if (response.body()?.results?.isNotEmpty() == true) {
                            binding.recyclerView.isVisible = true
                            tracks.addAll(response.body()?.results!!)
                            adapter.notifyDataSetChanged()
                        }
                        if (tracks.isEmpty()) {
                            showMessage(getString(R.string.nothing_found), R.drawable.no_found)
                        } else {
                            showMessage("", R.drawable.no_found)
                        }
                    } else {
                        showMessage(getString(R.string.something_went_wrong), R.drawable.disconnect)
                        binding.refreshButton.isVisible = true
                    }
                }

                override fun onFailure(call: Call<ITunesResponse>, t: Throwable) {
                    binding.progressBar.isVisible = false
                    showMessage(getString(R.string.something_went_wrong), R.drawable.disconnect)
                    binding.refreshButton.isVisible = true
                }

            })
        }
    }

    private fun addInHistory(track: Track) {
        searchHistory?.addInHistory(track, tracksInHistory)
        adapterHistory.notifyDataSetChanged()
    }

    private fun startAudioPlayer(track: Track) {
        val audioPlayerIntent = Intent(this, AudioPlayerActivity::class.java).apply {
            putExtra(SAVE_TRACK_FOR_AUDIO_PLAYER_KEY, track)
        }
        startActivity(audioPlayerIntent)
    }

    private fun hideAllExceptHistory() {
        tracks.clear()
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

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            mainThreadHandler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

}