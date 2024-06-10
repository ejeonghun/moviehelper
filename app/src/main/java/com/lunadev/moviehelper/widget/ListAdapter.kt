package com.lunadev.moviehelper.widget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lunadev.moviehelper.R
import com.lunadev.moviehelper.model.MovieElementAndInfo
import java.text.DecimalFormat

class ListAdapter : RecyclerView.Adapter<ListAdapter.ListViewHolder>() {
    private var listener: OnItemSelected? = null
    private var data: List<MovieElementAndInfo> = listOf()

    fun formatNumber(number: String): String {
        val formatter = DecimalFormat("#,###")
        return formatter.format(number.toInt())
    }

    fun updateData(data: List<MovieElementAndInfo>) {
        this.data = data.sortedBy { it.movieElement.rank } // 순위 순으로 정렬
        notifyDataSetChanged()
    }



    fun addListener(listener: OnItemSelected) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.movie_element, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(data[position])
    }

    fun interface OnItemSelected {
        fun onItemSelected(movieElementAndInfo: MovieElementAndInfo)
    }

    inner class ListViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private val textViewRank: TextView = v.findViewById(R.id.textViewMovieRank)
        private val textViewMovieNm: TextView = v.findViewById(R.id.textViewMovieNm)
        private val textViewOpenDt: TextView = v.findViewById(R.id.textViewOpenDt)
        private val textViewAudiAcc: TextView = v.findViewById(R.id.textViewAudiAcc)
        private val imageViewPoster: ImageView = v.findViewById(R.id.imageView)
        private val textViewAudiCnt: TextView = v.findViewById(R.id.textViewAudiCnt)

        init {
            v.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = data[position]
                    listener?.onItemSelected(item)
                }
            }
        }

        fun bind(movieElementAndInfo: MovieElementAndInfo) {
            val movieElement = movieElementAndInfo.movieElement
            val latestMovieInfo = movieElementAndInfo.movieinfo?.let { movieInfo ->
                val latestResult = movieInfo.data?.firstOrNull()?.result?.maxByOrNull { result ->
                    result?.repRlsDate?.toIntOrNull() ?: 0
                }

                latestResult?.let { result ->
                    val updatedData = movieInfo.data?.map { data ->
                        data?.copy(result = listOf(result))
                    }
                    movieInfo.copy(data = updatedData)
                }
            }
            if (movieElement.rank == 1) {
                // 왕관 아이콘
                textViewRank.text = "👑 ${movieElement.rank}위"
            } else {
                textViewRank.text = "${movieElement.rank}위"
            }
            textViewMovieNm.text = movieElement.movieNm
            textViewOpenDt.text = "\uD83D\uDCC5: ${movieElement.openDt}"
            textViewAudiCnt.text = "일일 관객 :${formatNumber(movieElement.audiCnt)}명"
            textViewAudiAcc.text = "누적 관객 :${formatNumber(movieElement.audiAcc.toString())}명"

            // 포스터 이미지는 Http 통신으로 받아오기 때문에 Glide 라이브러리 이용
            latestMovieInfo?.let { info ->
                val posterUrls = info.data?.firstOrNull()?.result?.firstOrNull()?.posters?.split("|")
                val firstPosterUrl = posterUrls?.firstOrNull()
                firstPosterUrl?.let {
                    Glide.with(itemView.context)
                        .load(firstPosterUrl)
                        .into(imageViewPoster)
                }
            }

        }
    }
}
