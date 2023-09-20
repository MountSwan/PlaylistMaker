package com.practicum.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson

class SearchHistory(private val tracksInHistory: ArrayList<Track>) {

    fun getTracksFromSharedPrefs(sharedPrefs: SharedPreferences): ArrayList<Track> {

        val historyInSharedPrefs = sharedPrefs.getString(HISTORY_SEARCH_KEY, null)

        if(historyInSharedPrefs != null) {
            val tracksFromSharedPrefs = Gson().fromJson(historyInSharedPrefs, Array<Track>::class.java)
            for(track in tracksFromSharedPrefs) {
                tracksInHistory.add(track)
            }
        }
        return tracksInHistory
    }

    fun putTracksInSharedPrefs(sharedPrefs: SharedPreferences) {
        sharedPrefs.edit()
            .putString(HISTORY_SEARCH_KEY, Gson().toJson(tracksInHistory))
            .apply()
    }

    fun addInHistory(track: Track): ArrayList<Track> {

        val iterator = tracksInHistory.iterator()
        while (iterator.hasNext()) {
            val nextTrack = iterator.next()
            if(nextTrack.trackId == track.trackId) {
                iterator.remove()
            }
        }

        if(tracksInHistory.size == 10) {
            tracksInHistory.removeAt(9)
        }

        tracksInHistory.add(0, track)

        return tracksInHistory

    }

    fun clearHistory(): ArrayList<Track> {
        tracksInHistory.clear()
        return tracksInHistory
    }
}