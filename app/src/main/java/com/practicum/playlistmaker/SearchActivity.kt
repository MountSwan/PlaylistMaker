package com.practicum.playlistmaker

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    companion object {
        const val SEARCH_REQUEST = "SEARCH_REQUEST"
        const val EXECUTED_REQUEST = 200
    }

    private var text: String = ""
    private val iTunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(ITunesApi::class.java)

    private val tracks = ArrayList<Track>()
    private var tracksInHistory = ArrayList<Track>()
    private val adapter = TrackAdapter() {
        addInHistory(it)
    }
    private val adapterHistory = TrackAdapter() {
        addInHistory(it)
    }

    private lateinit var binding: ActivitySearchBinding
    private var searchRequest: Editable? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager

        adapter.tracks = tracks

        val sharedPrefs: SharedPreferences = getSharedPreferences(PRACTICUM_EXAMPLE_PREFERENCES, MODE_PRIVATE)
        SearchHistory(tracksInHistory).getTracksFromSharedPrefs(sharedPrefs)
        adapterHistory.tracks = tracksInHistory

        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = adapter

        binding.recyclerViewOfHistory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewOfHistory.adapter = adapterHistory

        binding.inputEditText.setOnFocusChangeListener { view, hasFocus ->
            binding.historyOfSearch.isVisible = hasFocus && binding.inputEditText.text.isEmpty()
        }

        binding.arrowBackImage.setOnClickListener {
            finish()
        }

        binding.clearIcon.setOnClickListener {
            binding.inputEditText.setText("")
            inputMethodManager?.hideSoftInputFromWindow(binding.inputEditText.windowToken, 0)
            tracks.clear()
            adapter.notifyDataSetChanged()
            binding.placeholderMessage.isVisible = false
            binding.placeholderImage.isVisible = false
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(textSearch: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(textSearch: CharSequence?, start: Int, before: Int, count: Int) {
                binding.clearIcon.isVisible = clearButtonVisibility(textSearch)
                text = textSearch.toString()
                binding.historyOfSearch.isVisible = binding.inputEditText.hasFocus() && textSearch?.isEmpty() == true
            }

            override fun afterTextChanged(textSearch: Editable?) {
                // empty
            }
        }
        binding.inputEditText.addTextChangedListener(simpleTextWatcher)

        binding.inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
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
            SearchHistory(tracksInHistory).clearHistory()
            binding.historyOfSearch.isVisible = false
        }

    }

    override fun onStop() {
        super.onStop()

        val sharedPrefs: SharedPreferences = getSharedPreferences(PRACTICUM_EXAMPLE_PREFERENCES, MODE_PRIVATE)
        SearchHistory(tracksInHistory).putTracksInSharedPrefs(sharedPrefs)
    }

    private fun clearButtonVisibility(textSearch: CharSequence?): Boolean {
        return !textSearch.isNullOrEmpty()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_REQUEST,text)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        text = savedInstanceState.getString(SEARCH_REQUEST,"")
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
            iTunesService.search(searchRequest.toString()).enqueue(object :
                Callback<ITunesResponse> {
                override fun onResponse(call: Call<ITunesResponse>,
                                        response: Response<ITunesResponse>
                ) {
                    if (response.code() == EXECUTED_REQUEST) {
                        tracks.clear()
                        if (response.body()?.results?.isNotEmpty() == true) {
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
                    showMessage(getString(R.string.something_went_wrong), R.drawable.disconnect)
                    binding.refreshButton.isVisible = true
                }

            })
        }
    }

    private fun addInHistory(track: Track) {
        SearchHistory(tracksInHistory).addInHistory(track)
        adapterHistory.notifyDataSetChanged()
    }

}