package com.practicum.playlistmaker.library.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R

class PlaylistViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val ivCover: ImageView = itemView.findViewById(R.id.cover)
    private val tvPlaylistName: TextView = itemView.findViewById(R.id.playlistName)
    private val tvTracksNumber: TextView = itemView.findViewById(R.id.tracksNumber)

    fun bind(playlist: PlaylistUI) {
        val endOfTracksNumberText = defineEnd(playlist.numberOfTracksInPlaylist)
        Glide.with(itemView)
            .load(playlist.coverImage)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.four_space)))
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
