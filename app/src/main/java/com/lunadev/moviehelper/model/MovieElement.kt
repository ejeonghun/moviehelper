package com.lunadev.moviehelper.model

data class MovieElement(
    val rank: Int, // 순위
    val movieNm: String, // 영화명
    val openDt: String, // 개봉일
    val audiAcc: Int, // 누적관객수
    val rnum: String,
    val rankInten: String,
    val rankOldAndNew: String,
    val movieCd: String,
    val salesAmt: String,
    val salesShare: String,
    val salesInten: String,
    val salesChange: String,
    val salesAcc: String,
    val audiCnt: String,
    val audiInten: String,
    val audiChange: String,
    val scrnCnt: String,
    val showCnt: String,

    val MovieInfo: MovieInfo?
)

data class BoxOfficeResponse(
    val boxOfficeResult: BoxOfficeResult
)

data class BoxOfficeResult(
    val dailyBoxOfficeList: List<MovieElement>
)
// http://kobis.or.kr/kobisopenapi/homepg/apiservice/searchServiceInfo.do?serviceId=searchDailyBoxOffice