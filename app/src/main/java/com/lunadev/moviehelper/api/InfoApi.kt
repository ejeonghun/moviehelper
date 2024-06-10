package com.lunadev.moviehelper.api

import com.lunadev.moviehelper.model.MovieInfo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface InfoApi {
    // 영화 상세 정보 API
    @GET("search_json2.jsp")
    fun getMovieDetails(@Query("ServiceKey") ServiceKey: String,
                        @Query("collection") collection: String,
                        @Query("title") title:String,
                        @Query("detail") detail:String): Call<MovieInfo>

    // 검색 API
    @GET("search_json2.jsp")
    fun getMovieSearch(@Query("ServiceKey") ServiceKey: String,
                       @Query("collection") collection: String,
                       @Query("query") query:String,
                       @Query("detail") detail:String): Call<MovieInfo>
}
