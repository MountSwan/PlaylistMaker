package com.practicum.playlistmaker.data

import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker.HISTORY_SEARCH_KEY
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.presentation.search.SearchActivity.Companion.MAX_NUMBER_TRACKS_IN_SEARCH_HISTORY

class SearchHistory(private val sharedPrefs: SharedPreferences) {

    fun getTracksFromSharedPrefs(tracksInHistory: ArrayList<Track>) {

        val historyInSharedPrefs = sharedPrefs.getString(HISTORY_SEARCH_KEY, null)

        if (historyInSharedPrefs != null) {
            val tracksFromSharedPrefs =
                Gson().fromJson(historyInSharedPrefs, Array<Track>::class.java)
            tracksInHistory.addAll(tracksFromSharedPrefs)
        }

    }

    private fun putTracksInSharedPrefs(tracksInHistory: ArrayList<Track>) {
        sharedPrefs.edit()
            .putString(HISTORY_SEARCH_KEY, Gson().toJson(tracksInHistory))
            .apply()
    }

    fun addInHistory(track: Track, tracksInHistory: ArrayList<Track>) {

        tracksInHistory.removeIf { it.trackId == track.trackId }

        if (tracksInHistory.size == MAX_NUMBER_TRACKS_IN_SEARCH_HISTORY) {
            tracksInHistory.removeLast()
        }

        tracksInHistory.add(0, track)

        putTracksInSharedPrefs(tracksInHistory)

    }

    fun clearHistory(tracksInHistory: ArrayList<Track>) {
        tracksInHistory.clear()
        putTracksInSharedPrefs(tracksInHistory)
    }
}