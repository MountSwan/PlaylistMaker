package com.practicum.playlistmaker.search.data.network

import com.practicum.playlistmaker.search.domain.NetworkClient
import com.practicum.playlistmaker.search.domain.models.NetworkRequestState
import com.practicum.playlistmaker.search.domain.models.SearchState
import com.practicum.playlistmaker.search.domain.models.Track
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NetworkClientImpl : NetworkClient {

    companion object {
        const val EXECUTED_REQUEST = 200
    }

    private var networkRequestState: NetworkRequestState = NetworkRequestState.Default

    private val iTunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(ITunesApi::class.java)

    override fun doRequest(
        searchRequest: String,
        searchState: SearchState,
        tracks: ArrayList<Track>
    ) {
        networkRequestState = NetworkRequestState.Default
        iTunesService.search(searchRequest).enqueue(object :
            Callback<ITunesResponse> {
            override fun onResponse(
                call: Call<ITunesResponse>,
                response: Response<ITunesResponse>
            ) {
                if (response.code() == EXECUTED_REQUEST) {
                    tracks.clear()
                    networkRequestState = NetworkRequestState.OnResponse.ExecutedRequest
                    if (response.body()?.results?.isNotEmpty() == true) {
                        tracks.addAll(response.body()?.results!!.map {
                            Track(
                                trackId = it.trackId,
                                trackName = it.trackName,
                                artistName = it.artistName,
                                trackTimeMillis = it.trackTimeMillis,
                                trackTime = it.trackTime,
                                artworkUrl100 = it.artworkUrl100,
                                artworkUrl512 = it.artworkUrl512,
                                collectionName = it.collectionName,
                                releaseDate = it.releaseDate,
                                primaryGenreName = it.primaryGenreName,
                                country = it.country,
                                previewUrl = it.previewUrl
                            )
                        })

                        searchState.responseResultsIsNotEmpty = true
                    }
                } else {
                    networkRequestState = NetworkRequestState.OnResponse.IsNotExecutedRequest
                }
                searchState.requestIsComplete = true
            }

            override fun onFailure(call: Call<ITunesResponse>, t: Throwable) {
                networkRequestState = NetworkRequestState.OnFailure
                searchState.requestIsComplete = true
            }
        })
    }

    override fun networkRequestState(): NetworkRequestState {
        return networkRequestState
    }


}