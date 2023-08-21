package com.practicum.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val ivCover: ImageView = itemView.findViewById(R.id.cover)
    private val tvTrackName: TextView = itemView.findViewById(R.id.trackName)
    private val tvArtistName: TextView = itemView.findViewById(R.id.artistName)
    private val tvTrackTime: TextView = itemView.findViewById(R.id.trackTime)

    fun bind(model: Track) {
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.baseline_audiotrack)
            .centerCrop()
            .transform(RoundedCorners(10))
            .into(ivCover)
        tvTrackName.text = model.trackName
        tvArtistName.text = model.artistName
        tvTrackTime.text = model.trackTime
    }
}