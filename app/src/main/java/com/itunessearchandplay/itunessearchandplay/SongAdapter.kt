package com.itunessearchandplay.itunessearchandplay

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.IOException

class SongAdapter(private val mDataset: List<Song>): RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    private val mediaPlayer = MediaPlayer()
    private var currentPlaying = -1

    init {
        mediaPlayer.setOnErrorListener { mp, _, _ ->
            Log.i("SongAdapter", "MP error")
            mp.reset()
            currentPlaying = -1
            notifyDataSetChanged()
            false
        }

        mediaPlayer.setOnPreparedListener { mp->
            Log.i("SongAdapter", "MP started")
            mp.start()
        }

        mediaPlayer.setOnCompletionListener {
            Log.i("SongAdapter", "MP finished")
            mediaPlayer.release()
            currentPlaying = -1
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_song, parent, false)
        return SongViewHolder(view)
    }

    override fun getItemCount() = mDataset.size

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class SongViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val textViewSong: TextView = view.findViewById(R.id.text_view_song)
        private val textViewArtist: TextView = view.findViewById(R.id.text_view_artist)
        private val buttonPlay: ImageButton = view.findViewById(R.id.button_play)
        private val buttonStop: ImageButton = view.findViewById(R.id.button_stop)

        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {
            val title = mDataset[position].trackName
            val artist = mDataset[position].artistName
            textViewSong.text = "Title: $title"
            textViewArtist.text = "Artist: $artist"
            if(position == currentPlaying) {
                buttonPlay.visibility = View.GONE
                buttonStop.visibility = View.VISIBLE
            } else {
                buttonPlay.visibility = View.VISIBLE
                buttonStop.visibility = View.GONE
            }

            buttonStop.setOnClickListener {
                stopSong()
            }

            buttonPlay.setOnClickListener {
                playSong(position)
            }
        }
    }

    private fun playSong(position: Int) {
        val url = mDataset[position].previewUrl

        if(mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.reset()
        }

        try {
            Log.i("SongAdapter", url)
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepareAsync()
            currentPlaying = position
            notifyDataSetChanged()
        } catch (e: IllegalArgumentException) {
            Log.e("SongAdapter", e.toString())
        } catch (e: IllegalStateException) {
            Log.e("SongAdapter", e.toString())
        } catch (e: IOException) {
            Log.e("SongAdapter", e.toString())
        }
    }

    private fun stopSong() {
        if(mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.reset()
        }

        currentPlaying = -1
        notifyDataSetChanged()
    }

    fun releaseMediaPlayer() {
        if(mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        mediaPlayer.reset()
        mediaPlayer.release()
    }
}