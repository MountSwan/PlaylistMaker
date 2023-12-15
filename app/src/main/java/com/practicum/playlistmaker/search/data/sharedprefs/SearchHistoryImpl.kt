package com.practicum.playlistmaker.search.data.sharedprefs

import android.content.Context
import com.google.gson.Gson
import com.practicum.playlistmaker.HISTORY_SEARCH_KEY
import com.practicum.playlistmaker.PRACTICUM_EXAMPLE_PREFERENCES
import com.practicum.playlistmaker.search.domain.SearchHistory
import com.practicum.playlistmaker.search.domain.models.Track

class SearchHistoryImpl(context: Context) : SearchHistory {

    companion object {
        const val MAX_NUMBER_TRACKS_IN_SEARCH_HISTORY = 10
    }

    private val sharedPrefs = context.getSharedPreferences(
        PRACTICUM_EXAMPLE_PREFERENCES,
        Context.MODE_PRIVATE
    )

    override fun getTracksFromSharedPrefs(
        tracksInHistory: ArrayList<Track>
    ) {

        val historyInSharedPrefs = sharedPrefs.getString(HISTORY_SEARCH_KEY, null)

        if (historyInSharedPrefs != null) {
            val tracksFromSharedPrefs =
                Gson().fromJson(historyInSharedPrefs, Array<Track>::class.java)
            tracksInHistory.addAll(tracksFromSharedPrefs)
        }

    }

    private fun putTracksInSharedPrefs(
        tracksInHistory: ArrayList<Track>
    ) {
        sharedPrefs.edit()
            .putString(HISTORY_SEARCH_KEY, Gson().toJson(tracksInHistory))
            .apply()
    }

    override fun addInHistory(
        track: Track,
        tracksInHistory: ArrayList<Track>
    ) {

        tracksInHistory.removeIf { it.trackId == track.trackId }

        if (tracksInHistory.size == MAX_NUMBER_TRACKS_IN_SEARCH_HISTORY) {
            tracksInHistory.removeLast()
        }

        tracksInHistory.add(0, track)

        putTracksInSharedPrefs(tracksInHistory)

    }

    override fun clearHistory(tracksInHistory: ArrayList<Track>) {
        tracksInHistory.clear()
        putTracksInSharedPrefs(tracksInHistory)
    }
}