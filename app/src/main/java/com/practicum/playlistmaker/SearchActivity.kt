package com.practicum.playlistmaker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    companion object {
        const val SEARCH_REQUEST = "SEARCH_REQUEST"
    }

    private var text: String = ""
    private val iTunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(ITunesApi::class.java)

    private val tracks = ArrayList<Track>()
    private val adapter = TrackAdapter()

    private lateinit var termInput: EditText
    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderImage: ImageView
    private lateinit var refreshButton: Button
    private lateinit var tracksList: RecyclerView
    private lateinit var searchRequest: Editable


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val ivArrowBack = findViewById<ImageView>(R.id.arrow_back_image)
        val ivClearButton = findViewById<ImageView>(R.id.clearIcon)
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager

        placeholderMessage = findViewById(R.id.placeholderMessage)
        placeholderImage = findViewById(R.id.placeholderImage)
        refreshButton = findViewById(R.id.refreshButton)
        termInput = findViewById(R.id.inputEditText)
        tracksList = findViewById(R.id.recyclerView)

        adapter.tracks = tracks

        tracksList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        tracksList.adapter = adapter


        ivArrowBack.setOnClickListener {
            finish()
        }

        ivClearButton.setOnClickListener {
            termInput.setText("")
            inputMethodManager?.hideSoftInputFromWindow(termInput.windowToken, 0)
            tracks.clear()
            adapter.notifyDataSetChanged()
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(textSearch: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(textSearch: CharSequence?, start: Int, before: Int, count: Int) {
                ivClearButton.visibility = clearButtonVisibility(textSearch)
                text = textSearch.toString()
            }

            override fun afterTextChanged(textSearch: Editable?) {
                // empty
            }
        }
        termInput.addTextChangedListener(simpleTextWatcher)

        termInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchRequest = termInput.text
                search(searchRequest)

                true
            }
            false
        }

        refreshButton.setOnClickListener {
            search(searchRequest)
        }

    }
    private fun clearButtonVisibility(textSearch: CharSequence?): Int {
        return if (textSearch.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_REQUEST,text)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        text = savedInstanceState.getString(SEARCH_REQUEST,"")
        findViewById<EditText>(R.id.inputEditText).setText(text)
    }

    private fun showMessage(text: String, image: Int) {
        if (text.isNotEmpty()) {
            placeholderMessage.visibility = View.VISIBLE
            placeholderImage.visibility = View.VISIBLE
            tracks.clear()
            adapter.notifyDataSetChanged()
            placeholderMessage.text = text
            placeholderImage.setImageResource(image)
        } else {
            placeholderMessage.visibility = View.GONE
            placeholderImage.visibility = View.GONE
        }
    }

    private fun search(searchRequest: Editable) {
        if (searchRequest.isNotEmpty()) {
            refreshButton.visibility = View.GONE
            iTunesService.search(searchRequest.toString()).enqueue(object :
                Callback<ITunesResponse> {
                override fun onResponse(call: Call<ITunesResponse>,
                                        response: Response<ITunesResponse>
                ) {
                    if (response.code() == 200) {
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
                        refreshButton.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(call: Call<ITunesResponse>, t: Throwable) {
                    showMessage(getString(R.string.something_went_wrong), R.drawable.disconnect)
                    refreshButton.visibility = View.VISIBLE
                }

            })
        }
    }

}