package com.practicum.playlistmaker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(parent: ViewGroup) :
    RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.track_view, parent, false)) {
    private val ivCover: ImageView = itemView.findViewById(R.id.cover)
    private val tvTrackName: TextView = itemView.findViewById(R.id.trackName)
    private val tvArtistName: TextView = itemView.findViewById(R.id.artistName)
    private val tvTrackTime: TextView = itemView.findViewById(R.id.trackTime)

    fun bind(model: Track) {
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.album_cover_corner_radius)))
            .into(ivCover)
        tvTrackName.text = model.trackName
        tvArtistName.text = model.artistName
        tvTrackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(model.trackTimeMillis)
    }
}