package com.itunessearchandplay.itunessearchandplay.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.itunessearchandplay.itunessearchandplay.*
import com.itunessearchandplay.itunessearchandplay.activities.MainActivity
import com.itunessearchandplay.itunessearchandplay.fragments.AddToFavoritesDialog
import com.itunessearchandplay.itunessearchandplay.fragments.RemoveFromFavoritesDialog
import com.itunessearchandplay.itunessearchandplay.fragments.SongDetailsDialog
import com.itunessearchandplay.itunessearchandplay.models.Song
import java.io.IOException

/**
 * Author: Andreas Pribitzer
 */

class SongAdapter(private val context: Context, private var mDataset: List<Song>): RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    companion object {
        private const val KEY_FAVORITES = "favorites"
    }

    private val mediaPlayer = MediaPlayer()
    private var currentPlaying = -1
    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    private val gson = Gson()
    private var favorites: ArrayList<Song> = if(prefs.contains(
            KEY_FAVORITES
        )) {
        val jsonText = prefs.getString(KEY_FAVORITES, null)
        gson.fromJson<ArrayList<Song>>(jsonText, object: TypeToken<ArrayList<Song>>(){}.type)
    } else {
        ArrayList()
    }

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
            else
                mp.reset()
        }

        mediaPlayer.setOnCompletionListener {mp->
            mp.reset()
            currentPlaying = -1
            notifyDataSetChanged()
        }
    }

    fun setDataset(dataset: ArrayList<Song>) {
        mDataset = dataset
        notifyDataSetChanged()
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
        private val songItem: ConstraintLayout = view.findViewById(R.id.song_item)

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

            songItem.setOnClickListener {
                val song = mDataset[position]
                val dialog =
                    SongDetailsDialog.newInstance(
                        song.trackName,
                        song.artistName,
                        song.collectionName,
                        song.primaryGenreName,
                        song.trackTimeMillis
                    )
                dialog.show((context as MainActivity).supportFragmentManager, "Details")
            }

            songItem.setOnLongClickListener {
                if(!favorites.contains(mDataset[position])) {
                    val dialog =
                        AddToFavoritesDialog.newInstance(
                            position
                        )
                    dialog.setListener(object:
                        AddToFavoritesDialog.IAddToFavorites {
                        override fun addToFavorites(position: Int) {
                            favorites.add(mDataset[position])
                            val jsonText = gson.toJson(favorites)
                            prefs.edit().putString(KEY_FAVORITES, jsonText).apply()
                        }
                    })
                    dialog.show((context as MainActivity).supportFragmentManager, "AddToFavorites")
                } else {
                    val dialog =
                        RemoveFromFavoritesDialog.newInstance(
                            position
                        )
                    dialog.setListener(object :
                        RemoveFromFavoritesDialog.IRemoveFromFavorites {
                        override fun removeFromFavorites(position: Int) {
                            favorites.remove(mDataset[position])
                            val jsonText = gson.toJson(favorites)
                            prefs.edit().putString(KEY_FAVORITES, jsonText).apply()
                        }
                    })
                    dialog.show((context as MainActivity).supportFragmentManager, "RemoveFromFavorites")
                }
                true
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

    fun reloadFavorites() {
        favorites = if(prefs.contains(KEY_FAVORITES)) {
            val jsonText = prefs.getString(KEY_FAVORITES, null)
            gson.fromJson<ArrayList<Song>>(jsonText, object: TypeToken<ArrayList<Song>>(){}.type)
        } else {
            ArrayList()
        }
    }
}