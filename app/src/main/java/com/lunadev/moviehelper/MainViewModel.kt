package com.lunadev.moviehelper

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lunadev.moviehelper.api.RetrofitClient
import com.lunadev.moviehelper.model.BoxOfficeResponse
import com.lunadev.moviehelper.model.MovieElement
import com.lunadev.moviehelper.model.MovieElementAndInfo
import com.lunadev.moviehelper.model.MovieInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel: ViewModel() {
    val movieRouteValue = MutableLiveData<String>() // 영화 상세 정보 요청을 위한 영화 코드 값
    val selectedDate = MutableLiveData<String>() // 박스오피스 API 요청을 위한 DATE 값
    private val kobisApiKey = BuildConfig.kobis_API_KEY
    private val kmdbApiKey = BuildConfig.kmdb_API_KEY

    private val _dailyBoxOfficeList = MutableLiveData<List<MovieElementAndInfo>>()
    val dailyBoxOfficeList: LiveData<List<MovieElementAndInfo>> get() = _dailyBoxOfficeList

    private val _selectedMovieElement = MutableLiveData<MovieElement?>()
    val selectedMovieElement: LiveData<MovieElement?> get() = _selectedMovieElement
    fun setSelectedMovieElement(movieElement: MovieElement?) {
        _selectedMovieElement.value = movieElement
    }

    private val _selectedMovieInfo = MutableLiveData<MovieInfo?>()
    val selectedMovieInfo: LiveData<MovieInfo?> get() = _selectedMovieInfo
    fun setSelectedMovieInfo(movieInfo: MovieInfo?) {
        _selectedMovieInfo.value = movieInfo
    }

    private val _selectedMovieResult = MutableLiveData<List<MovieInfo.Data.Result>?>()
    val selectedMovieResult: LiveData<List<MovieInfo.Data.Result>?> get() = _selectedMovieResult

    fun setSelectedMovieResult(movieResults: List<MovieInfo.Data.Result>?) {
        _selectedMovieResult.value = movieResults
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel() // 모든 네트워크 요청 취소
    }

    fun getRank(date: String) = viewModelScope.launch(Dispatchers.IO) {
        RetrofitClient.rankApi.getDailyRank(kobisApiKey, date)
            .enqueue(object : Callback<BoxOfficeResponse> {
                override fun onResponse(
                    call: Call<BoxOfficeResponse>,
                    response: Response<BoxOfficeResponse>
                ) {
                    if (response.isSuccessful && response.code() == 200) {
                        val movieElements = response.body()?.boxOfficeResult?.dailyBoxOfficeList ?: emptyList()

                        val movieElementAndInfos = mutableListOf<MovieElementAndInfo>()

                        movieElements.forEach { movieElement ->
                            getMovieDetails(movieElement) { movieInfo ->
                                movieInfo?.let {
                                    val movieElementAndInfo =
                                        MovieElementAndInfo(movieElement, movieInfo)
                                    movieElementAndInfos.add(movieElementAndInfo)

                                    // Check if all movie info is fetched
                                    if (movieElementAndInfos.size == movieElements.size) {
                                        _dailyBoxOfficeList.postValue(movieElementAndInfos)
                                    }
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<BoxOfficeResponse>, t: Throwable) {
                    t.printStackTrace()
                }
            })
    }

    private fun getMovieDetails(movieElement: MovieElement, callback: (MovieInfo?) -> Unit) {
//        val formattedTitle = movieElement.movieNm.replace(" ", "%20")
//        val url = "http://api.koreafilm.or.kr/openapi-data2/wisenut/search_api/search_json2.jsp?collection=kmdb_new2&ServiceKey=$kmdbApiKey&title=${movieElement.movieNm}"
        RetrofitClient.infoApi.getMovieDetails(ServiceKey = kmdbApiKey, collection = "kmdb_new2", title = movieElement.movieNm, detail = "Y").enqueue(object : Callback<MovieInfo> {
            override fun onResponse(call: Call<MovieInfo>, response: Response<MovieInfo>) {
                if (response.isSuccessful && response.code() == 200) {
                    Log.d("MainViewModel" ,"${response.body()?.data?.firstOrNull()?.result?.getOrNull(0)?.toString()}")
                    val movieInfo = response.body()
                    callback(movieInfo)
//                    val posterUrls = response.body()?.data?.firstOrNull()?.result?.getOrNull(0)?.posters?.split("|")
//                    val firstPosterUrl = posterUrls?.firstOrNull()
//                    Log.d("MainViewModel", "posterUrl: $firstPosterUrl")
                } else {
                    callback(null)
                }
            }

            override fun onFailure(call: Call<MovieInfo>, t: Throwable) {
                t.printStackTrace()
                callback(null)
            }
        })
    }


    fun getMovieSearchs(query: String, callback: (List<MovieInfo.Data.Result?>) -> Unit) { // 검색 API
        val kmdbApiKey = "HF11HZ34DT5YS02661W9"
        RetrofitClient.infoApi.getMovieSearch(
            ServiceKey = kmdbApiKey,
            collection = "kmdb_new2",
            query = query,
            detail = "Y"
        ).enqueue(object : Callback<MovieInfo> {
            override fun onResponse(call: Call<MovieInfo>, response: Response<MovieInfo>) {
                if (response.isSuccessful && response.body() != null) {
                    val movieResults = response.body()?.data?.flatMap { data ->
                        data?.result.orEmpty()
                    }.orEmpty()
                    callback(movieResults)
                } else {
                    callback(emptyList())
                }
            }

            override fun onFailure(call: Call<MovieInfo>, t: Throwable) {
                t.printStackTrace()
                callback(emptyList())
            }
        })
    }

}