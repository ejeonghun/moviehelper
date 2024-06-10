package com.lunadev.moviehelper.model


import com.google.gson.annotations.SerializedName

data class MovieInfo(
    @SerializedName("Data")
    val `data`: List<Data?>?,
    @SerializedName("KMAQuery")
    val kMAQuery: String?,
    @SerializedName("Query")
    val query: String?,
    @SerializedName("TotalCount")
    val totalCount: Int?
) {
    data class Data(
        @SerializedName("CollName")
        val collName: String?,
        @SerializedName("Count")
        val count: Int?,
        @SerializedName("Result")
        val result: List<Result?>?,
        @SerializedName("TotalCount")
        val totalCount: Int?
    ) {
        data class Result(
            @SerializedName("ALIAS")
            val aLIAS: String?,
            val actors: Actors?,
            val audiAcc: String?,
            @SerializedName("Awards1")
            val awards1: String?,
            @SerializedName("Awards2")
            val awards2: String?,
            @SerializedName("Codes")
            val codes: Codes?,
            @SerializedName("CommCodes")
            val commCodes: CommCodes?,
            val company: String?,
            @SerializedName("DOCID")
            val dOCID: String?,
            val directors: Directors?,
            val episodes: String?,
            val fLocation: String?,
            val genre: String?,
            val keywords: String?,
            val kmdbUrl: String?,
            val modDate: String?,
            val movieId: String?,
            val movieSeq: String?,
            val nation: String?,
            val openThtr: String?,
            val plots: Plots?,
            val posters: String?,
            val prodYear: String?,
            val ratedYn: String?,
            val rating: String?,
            val ratings: Ratings?,
            val regDate: String?,
            val repRatDate: String?,
            val repRlsDate: String?,
            val runtime: String?,
            val salesAcc: String?,
            val screenArea: String?,
            val screenCnt: String?,
            val soundtrack: String?,
            val staffs: Staffs?,
            val stat: List<Stat?>?,
            val statDate: String?,
            val statSouce: String?,
            val stlls: String?,
            val themeSong: String?,
            val title: String?,
            val titleEng: String?,
            val titleEtc: String?,
            val titleOrg: String?,
            val type: String?,
            val use: String?,
            val vods: Vods?
        ) {
            data class Actors(
                val actor: List<Actor?>?
            ) {
                data class Actor(
                    val actorEnNm: String?,
                    val actorId: String?,
                    val actorNm: String?
                )
            }

            data class Codes(
                @SerializedName("Code")
                val code: List<Code?>?
            ) {
                data class Code(
                    @SerializedName("CodeNm")
                    val codeNm: String?,
                    @SerializedName("CodeNo")
                    val codeNo: String?
                )
            }

            data class CommCodes(
                @SerializedName("CommCode")
                val commCode: List<CommCode?>?
            ) {
                data class CommCode(
                    @SerializedName("CodeNm")
                    val codeNm: String?,
                    @SerializedName("CodeNo")
                    val codeNo: String?
                )
            }

            data class Directors(
                val director: List<Director?>?
            ) {
                data class Director(
                    val directorEnNm: String?,
                    val directorId: String?,
                    val directorNm: String?
                )
            }

            data class Plots(
                val plot: List<Plot?>?
            ) {
                data class Plot(
                    val plotLang: String?,
                    val plotText: String?
                )
            }

            data class Ratings(
                val rating: List<Rating?>?
            ) {
                data class Rating(
                    val ratingDate: String?,
                    val ratingGrade: String?,
                    val ratingMain: String?,
                    val ratingNo: String?,
                    val releaseDate: String?,
                    val runtime: String?
                )
            }

            data class Staffs(
                val staff: List<Staff?>?
            ) {
                data class Staff(
                    val staffEnNm: String?,
                    val staffEtc: String?,
                    val staffId: String?,
                    val staffNm: String?,
                    val staffRole: String?,
                    val staffRoleGroup: String?
                )
            }

            data class Stat(
                val audiAcc: String?,
                val salesAcc: String?,
                val screenArea: String?,
                val screenCnt: String?,
                val statDate: String?,
                val statSouce: String?
            )

            data class Vods(
                val vod: List<Vod?>?
            ) {
                data class Vod(
                    val vodClass: String?,
                    val vodUrl: String?
                )
            }
        }
    }
}