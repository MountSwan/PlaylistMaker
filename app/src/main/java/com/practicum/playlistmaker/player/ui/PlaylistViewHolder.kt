package com.practicum.playlistmaker.player.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.library.ui.PlaylistUI

class PlaylistViewHolder(parent: ViewGroup) :
    RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.bottom_playlist_view, parent, false)
    ) {
    private val ivCover: ImageView = itemView.findViewById(R.id.cover)
    private val tvPlaylistName: TextView = itemView.findViewById(R.id.playlistName)
    private val tvTracksNumber: TextView = itemView.findViewById(R.id.tracksNumber)

    fun bind(playlist: PlaylistUI) {
        val endOfTracksNumberText = defineEnd(playlist.numberOfTracksInPlaylist)
        Glide.with(itemView)
            .load(playlist.coverImage)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.album_cover_corner_radius)))
            .into(ivCover)
        tvPlaylistName.text = playlist.playlistName
        tvTracksNumber.text = "${playlist.numberOfTracksInPlaylist} $endOfTracksNumberText"
    }

    private fun defineEnd(number: Int): String {
        return if (number < 10) {
            when (number) {
                1 -> "трек"
                2, 3, 4 -> "трека"
                else -> "треков"
            }
        } else if (number.toString()[number.toString().length - 2].digitToInt() == 1) {
            "треков"
        } else {
            when (number.toString().last().digitToInt()) {
                1 -> "трек"
                2, 3, 4 -> "трека"
                else -> "треков"
            }
        }

    }
}