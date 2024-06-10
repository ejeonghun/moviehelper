package com.lunadev.moviehelper.api

import com.lunadev.moviehelper.model.BoxOfficeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RankApi {
    // 박스오피스 API
    @GET("searchDailyBoxOfficeList.json")
    fun getDailyRank(
        @Query("key") apiKey: String,
        @Query("targetDt") date: String
    ): Call<BoxOfficeResponse>
}
