package com.lunadev.moviehelper.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL_KOBI = "http://www.kobis.or.kr/kobisopenapi/webservice/rest/boxoffice/"
private const val BASE_URL_KMDB = "http://api.koreafilm.or.kr/openapi-data2/wisenut/search_api/"

object RetrofitClient {
    private val kobisClient = Retrofit.Builder()
        .baseUrl(BASE_URL_KOBI)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val rankApi: RankApi = kobisClient.create(RankApi::class.java)

    private val kmdbClient = Retrofit.Builder()
        .baseUrl(BASE_URL_KMDB)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val infoApi: InfoApi = kmdbClient.create(InfoApi::class.java)
}