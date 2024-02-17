package com.practicum.playlistmaker.search.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.models.TrackDto
import com.practicum.playlistmaker.search.domain.models.NetworkRequestState
import com.practicum.playlistmaker.search.domain.models.SearchState
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NetworkClientImpl(private val iTunesService: ITunesApi, private val context: Context) :
    NetworkClient {

    private var networkRequestState: NetworkRequestState = NetworkRequestState.Default

    override suspend fun doRequest(
        searchRequest: String,
        searchState: SearchState,
        tracks: ArrayList<Track>,
        tracksResponse: ArrayList<TrackDto>
    ): NetworkRequestState {
        networkRequestState = NetworkRequestState.Default
        tracksResponse.clear()

        if (!isConnected()) {
            networkRequestState = NetworkRequestState.OnFailure
            return networkRequestState
        }

        return withContext(Dispatchers.IO) {

                val response = iTunesService.search(searchRequest)
                try {
                    tracks.clear()
                    networkRequestState = NetworkRequestState.OnResponse.ExecutedRequest
                    tracksResponse.addAll(response.results)
                    return@withContext networkRequestState
                } catch (e: Throwable) {
                    networkRequestState = NetworkRequestState.OnResponse.IsNotExecutedRequest
                    return@withContext networkRequestState

            }
        }

    }

    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }

}