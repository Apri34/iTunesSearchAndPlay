package com.itunessearchandplay.itunessearchandplay.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.itunessearchandplay.itunessearchandplay.*
import com.itunessearchandplay.itunessearchandplay.adapters.SongAdapter
import com.itunessearchandplay.itunessearchandplay.models.Song
import com.itunessearchandplay.itunessearchandplay.models.SongResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Author: Andreas Pribitzer
 */

class MainActivity : AppCompatActivity() {

    companion object {
        private const val BASE_URL = "https://itunes.apple.com/"
    }

    private lateinit var retrofit: Retrofit
    private lateinit var apiService: ITunesApiService

    private lateinit var recyclerView: RecyclerView
    private lateinit var toolbar: Toolbar

    private var songs = ArrayList<Song>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        recyclerView = findViewById(R.id.recycler_view_main)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(recyclerView.context, LinearLayoutManager.VERTICAL))
        }

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ITunesApiService::class.java)
    }

    override fun onResume() {
        super.onResume()
        (recyclerView.adapter as SongAdapter?)?.reloadFavorites()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        val search: MenuItem = menu.findItem(R.id.search)
        val searchView = search.actionView as SearchView

        searchView.queryHint = getString(R.string.search_hint)
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(query == null) return true

                val songCall: Call<SongResponse> = apiService.getSongs(query)
                songCall.enqueue(object: Callback<SongResponse> {
                    override fun onFailure(call: Call<SongResponse>, t: Throwable) {
                        Toast.makeText(this@MainActivity,
                            R.string.toast_failure, Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(call: Call<SongResponse>, response: Response<SongResponse>) {
                        songs = response.body()!!.results as ArrayList<Song>
                        if(songs.size == 0)
                            Toast.makeText(this@MainActivity,
                                R.string.no_songs_found, Toast.LENGTH_LONG).show()

                        (recyclerView.adapter as SongAdapter?)?.resetMediaPlayer()
                        if(recyclerView.adapter != null) {
                            (recyclerView.adapter as SongAdapter).setDataset(songs)
                        } else {
                            recyclerView.adapter =
                                SongAdapter(
                                    this@MainActivity,
                                    songs
                                )
                        }
                    }
                })

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean { return false }

        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.favorites) {
            startActivity(Intent(this, FavoritesActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStop() {
        super.onStop()
        (recyclerView.adapter as SongAdapter?)?.resetMediaPlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        (recyclerView.adapter as SongAdapter?)?.releaseMediaPlayer()
    }
}
