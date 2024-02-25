package com.practicum.playlistmaker.search.data.sharedprefs

import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker.HISTORY_SEARCH_KEY
import com.practicum.playlistmaker.library.data.db.AppDatabase
import com.practicum.playlistmaker.search.data.SearchHistory
import com.practicum.playlistmaker.search.domain.models.Track

class SearchHistoryImpl(
    private val sharedPrefs: SharedPreferences,
    private val gson: Gson,
    private val appDatabase: AppDatabase,
) :
    SearchHistory {

    private val maxNumberTracksInSearchHistory = 10

    override suspend fun getTracksFromSharedPrefs(
        tracksInHistory: ArrayList<Track>
    ) {

        val historyInSharedPrefs = sharedPrefs.getString(HISTORY_SEARCH_KEY, null)

        if (historyInSharedPrefs != null) {
            val tracksFromSharedPrefs =
                gson.fromJson(historyInSharedPrefs, Array<Track>::class.java)
            tracksInHistory.clear()
            tracksInHistory.addAll(tracksFromSharedPrefs)
            tracksInHistory.map {
                it.isFavorite = checkIsFavorite(it.trackId)
            }
        }

    }

    private suspend fun checkIsFavorite(trackId: Long?): Boolean {
        appDatabase.favoriteTrackDao().getFavoriteTracksId()
            .forEach { favoriteTrackId -> if (favoriteTrackId == trackId) return true }
        return false
    }

    private fun putTracksInSharedPrefs(
        tracksInHistory: ArrayList<Track>
    ) {
        sharedPrefs.edit()
            .putString(HISTORY_SEARCH_KEY, gson.toJson(tracksInHistory))
            .apply()
    }

    override fun addInHistory(
        track: Track,
        tracksInHistory: ArrayList<Track>
    ) {

        tracksInHistory.removeIf { it.trackId == track.trackId }

        if (tracksInHistory.size == maxNumberTracksInSearchHistory) {
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