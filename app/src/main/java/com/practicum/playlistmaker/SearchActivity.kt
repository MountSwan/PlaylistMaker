package com.practicum.playlistmaker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView

class SearchActivity : AppCompatActivity() {

    companion object {
        const val SEARCH_REQUEST = "SEARCH_REQUEST"
    }

    private var text: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val ivArrowBack = findViewById<ImageView>(R.id.arrow_back_image)
        val inputEditText = findViewById<EditText>(R.id.inputEditText)
        val ivClearButton = findViewById<ImageView>(R.id.clearIcon)
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager


        ivArrowBack.setOnClickListener {
            finish()
        }

        ivClearButton.setOnClickListener {
            inputEditText.setText("")
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
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
        inputEditText.addTextChangedListener(simpleTextWatcher)

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

}