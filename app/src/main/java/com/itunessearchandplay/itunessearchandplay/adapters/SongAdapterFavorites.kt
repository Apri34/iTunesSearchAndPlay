package com.itunessearchandplay.itunessearchandplay.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.itunessearchandplay.itunessearchandplay.activities.FavoritesActivity
import com.itunessearchandplay.itunessearchandplay.R
import com.itunessearchandplay.itunessearchandplay.fragments.RemoveFromFavoritesDialog
import com.itunessearchandplay.itunessearchandplay.models.Song
import java.io.IOException

/**
 * Author: Andreas Pribitzer
 */

class SongAdapterFavorites(private val context: Context, private val mDataset: ArrayList<Song>): RecyclerView.Adapter<SongAdapterFavorites.SongFavoriteViewHolder>() {

    companion object {
        private const val KEY_FAVORITES = "favorites"
    }

    private val mediaPlayer = MediaPlayer()
    private var currentPlaying = -1
    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    private val gson = Gson()

    init {
        mediaPlayer.setOnErrorListener { mp, _, _ ->
            mp.reset()
            currentPlaying = -1
            notifyDataSetChanged()
            false
        }

        mediaPlayer.setOnPreparedListener { mp->
            if(currentPlaying != -1)
                mp.start()
        }

        mediaPlayer.setOnCompletionListener { mp->
            mp.reset()
            currentPlaying = -1
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongFavoriteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_song_favorite, parent, false)
        return SongFavoriteViewHolder(view)
    }

    override fun getItemCount() = mDataset.size

    override fun onBindViewHolder(holder: SongFavoriteViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class SongFavoriteViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val textViewSong: TextView = view.findViewById(R.id.text_view_song_favorites)
        private val textViewArtist: TextView = view.findViewById(R.id.text_view_artist_favorites)
        private val buttonPlay: ImageButton = view.findViewById(R.id.button_play_favorites)
        private val buttonStop: ImageView = view.findViewById(R.id.button_stop_favorites)
        private val buttonRemove: ImageView = view.findViewById(R.id.button_remove_from_favorites)

        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {
            val title = mDataset[position].trackName
            val artist = mDataset[position].artistName
            textViewSong.text = "Title: $title"
            textViewArtist.text = "Artist: $artist"
            if(position == currentPlaying) {
                buttonPlay.visibility = View.INVISIBLE
                buttonStop.visibility = View.VISIBLE
            } else {
                buttonPlay.visibility = View.VISIBLE
                buttonStop.visibility = View.INVISIBLE
            }

            buttonStop.setOnClickListener {
                stopSong()
            }

            buttonPlay.setOnClickListener {
                playSong(position)
            }

            buttonRemove.setOnClickListener {
                val dialog =
                    RemoveFromFavoritesDialog.newInstance(
                        position
                    )
                dialog.setListener(object :
                    RemoveFromFavoritesDialog.IRemoveFromFavorites {
                    override fun removeFromFavorites(position: Int) {
                        if(position == currentPlaying)
                            stopSong()
                        mDataset.remove(mDataset[position])
                        val jsonText = gson.toJson(mDataset)
                        prefs.edit().putString(KEY_FAVORITES, jsonText).apply()
                        notifyDataSetChanged()
                    }
                })
                dialog.show((context as FavoritesActivity).supportFragmentManager, "RemoveFromFavorites")
            }
        }

        private fun playSong(position: Int) {
            val url = mDataset[position].previewUrl

            if(mediaPlayer.isPlaying) {
                mediaPlayer.stop()
                mediaPlayer.reset()
            }

            try {
                Log.i("SongAdapterFavorites", url)
                mediaPlayer.setDataSource(url)
                mediaPlayer.prepareAsync()
                currentPlaying = position
                notifyDataSetChanged()
            } catch (e: IllegalArgumentException) {
                Toast.makeText(context, context.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
            } catch (e: IllegalStateException) {
                Toast.makeText(context, context.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                Toast.makeText(context, context.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
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
    }

    fun resetMediaPlayer() {
        if(mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        mediaPlayer.reset()
        currentPlaying = -1
        notifyDataSetChanged()
    }

    fun releaseMediaPlayer() {
        mediaPlayer.release()
    }
}