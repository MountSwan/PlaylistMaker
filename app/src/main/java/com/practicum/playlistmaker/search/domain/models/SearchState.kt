package com.practicum.playlistmaker.search.domain.models

data class SearchState(
    var refreshButtonIsVisible:Boolean,
    var recyclerViewIsVisible:Boolean,
    var progressBarIsVisible:Boolean,
    var adapterNotifyDataSetChanged: Boolean,
    var clearButtonVisibility: Boolean,
    var tracksInHistorySize: Int,
    var requestIsComplete: Boolean,
    var responseResultsIsNotEmpty: Boolean,
)
