package com.itunessearchandplay.itunessearchandplay

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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    companion object {
        private const val BASE_URL = "https://itunes.apple.com/"

        private const val KEY_LIST_SONGS = "songs"
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

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(recyclerView.context, LinearLayoutManager.VERTICAL))
        }

        if(savedInstanceState != null) {
            songs = savedInstanceState.getParcelableArrayList<Song>(KEY_LIST_SONGS) as ArrayList<Song>
            recyclerView.adapter = SongAdapter(songs)
        }

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ITunesApiService::class.java)
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
                        Toast.makeText(this@MainActivity, R.string.toast_failure, Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(call: Call<SongResponse>, response: Response<SongResponse>) {
                        songs = response.body()!!.results as ArrayList<Song>
                        if(songs.size == 0)
                            Toast.makeText(this@MainActivity, R.string.no_songs_found, Toast.LENGTH_LONG).show()

                        recyclerView.adapter = SongAdapter(songs)
                    }
                })

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean { return false }

        })
        return true
    }

    override fun onStop() {
        super.onStop()
        (recyclerView.adapter as SongAdapter).releaseMediaPlayer()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putParcelableArrayList(KEY_LIST_SONGS, songs)
    }
}
