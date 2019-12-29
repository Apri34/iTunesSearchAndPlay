package com.itunessearchandplay.itunessearchandplay.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.itunessearchandplay.itunessearchandplay.R

class SongDetailsDialog: DialogFragment() {

    companion object {
        private const val KEY_SONG = "song"
        private const val KEY_ARTIST = "artist"
        private const val KEY_COLLECTION = "collection"
        private const val KEY_GENRE = "genre"
        private const val KEY_TIME = "time"

        fun newInstance(song: String, artist: String, collection: String, genre: String, time: Int): SongDetailsDialog {
            val fragment = SongDetailsDialog()
            val args = Bundle()
            args.putString(KEY_SONG, song)
            args.putSerializable(KEY_ARTIST, artist)
            args.putString(KEY_COLLECTION, collection)
            args.putString(KEY_GENRE, genre)
            args.putInt(KEY_TIME, time)
            fragment.arguments = args
            return fragment
        }
    }

    @SuppressLint("InflateParams", "SetTextI18n")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val song = arguments?.getString(KEY_SONG)
        val artist = arguments?.getString(KEY_ARTIST)
        val collection = arguments?.getString(KEY_COLLECTION)
        val genre = arguments?.getString(KEY_GENRE)
        val time = arguments?.getInt(KEY_TIME)

        val view = LayoutInflater.from(context)
            .inflate(R.layout.fragment_song_details, null)

        val textViewSong: TextView = view.findViewById(R.id.text_view_details_song)
        val textViewArtist: TextView = view.findViewById(R.id.text_view_details_artist)
        val textViewGenre: TextView = view.findViewById(R.id.text_view_details_genre)
        val textViewCollection: TextView = view.findViewById(R.id.text_view_details_collection)
        val textViewTime: TextView = view.findViewById(R.id.text_view_details_time)

        textViewSong.text = "Song: $song"
        textViewArtist.text = "Artist: $artist"
        textViewCollection.text = "Collection: $collection"
        textViewGenre.text = "Genre: $genre"
        if(time != null) {
            var seconds = time.div(1000)
            val minutes = seconds / 60
            seconds -= minutes*60
            textViewTime.text = String.format("Time: %02d:%02d", minutes, seconds)
        }

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Details")
            .setView(view)
            .setNeutralButton(android.R.string.ok) {_,_->}
        return builder.create()
    }
}