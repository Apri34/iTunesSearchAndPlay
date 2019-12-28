package com.itunessearchandplay.itunessearchandplay

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApiService {

    @GET("search")
    fun getSongs(@Query("term") term: String): Call<SongResponse>
}