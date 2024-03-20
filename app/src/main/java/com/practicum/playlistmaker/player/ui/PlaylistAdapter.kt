package com.practicum.playlistmaker.player.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.library.ui.PlaylistUI

class PlaylistAdapter(private val clickListener: PlaylistClickListener) :
    RecyclerView.Adapter<PlaylistViewHolder>() {

    var playlists = ArrayList<PlaylistUI>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder =
        PlaylistViewHolder(parent)

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener { clickListener.onPlaylistClick(playlists[position]) }
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    fun interface PlaylistClickListener {
        fun onPlaylistClick(playlist: PlaylistUI)
    }

}