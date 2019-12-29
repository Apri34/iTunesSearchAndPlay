package com.itunessearchandplay.itunessearchandplay

import com.itunessearchandplay.itunessearchandplay.models.SongResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Author: Andreas Pribitzer
 */

interface ITunesApiService {

    @GET("search")
    fun getSongs(@Query("term") term: String): Call<SongResponse>
}