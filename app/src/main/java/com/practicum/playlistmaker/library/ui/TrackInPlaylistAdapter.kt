package com.practicum.playlistmaker.library.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.TrackViewHolder

class TrackInPlaylistAdapter(
    private val clickListener: TrackClickListener,
    private val onLongClickListener: OnLongTrackClickListener,
) :
    RecyclerView.Adapter<TrackViewHolder>() {

    var tracks = ArrayList<Track>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder =
        TrackViewHolder(parent)

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener { clickListener.onTrackClick(tracks[position]) }
        holder.itemView.setOnLongClickListener {
            onLongClickListener.onLongTrackClick(tracks[position])
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    fun interface TrackClickListener {
        fun onTrackClick(track: Track)
    }

    fun interface OnLongTrackClickListener {
        fun onLongTrackClick(track: Track)
    }

}