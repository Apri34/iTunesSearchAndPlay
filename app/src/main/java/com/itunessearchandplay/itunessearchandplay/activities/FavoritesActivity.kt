package com.itunessearchandplay.itunessearchandplay.activities

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.itunessearchandplay.itunessearchandplay.R
import com.itunessearchandplay.itunessearchandplay.models.Song
import com.itunessearchandplay.itunessearchandplay.adapters.SongAdapterFavorites

class FavoritesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var textViewNoFavorites: TextView

    private lateinit var toolbar: Toolbar

    companion object {
        private const val KEY_FAVORITES = "favorites"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        toolbar = findViewById(R.id.toolbar_favorites)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.favorites)

        textViewNoFavorites = findViewById(R.id.text_view_no_favorites)
        recyclerView = findViewById(R.id.recycler_view_favorites)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@FavoritesActivity)
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(recyclerView.context, LinearLayoutManager.VERTICAL))
        }

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val gson = Gson()

        if(prefs.contains(KEY_FAVORITES)) {
            val jsonText = prefs.getString(KEY_FAVORITES, null)
            val favorites: ArrayList<Song> = gson.fromJson<ArrayList<Song>>(jsonText, object: TypeToken<ArrayList<Song>>(){}.type)
            if(favorites.isNotEmpty())
                recyclerView.adapter =
                    SongAdapterFavorites(
                        this,
                        favorites
                    )
            else
                textViewNoFavorites.visibility = View.VISIBLE
        } else
            textViewNoFavorites.visibility = View.VISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_favorites, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if(item?.itemId == R.id.back) {
            finish()
            true
        } else
            super.onOptionsItemSelected(item)
    }

    override fun onStop() {
        super.onStop()
        (recyclerView.adapter as SongAdapterFavorites?)?.resetMediaPlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        (recyclerView.adapter as SongAdapterFavorites?)?.releaseMediaPlayer()
    }
}
