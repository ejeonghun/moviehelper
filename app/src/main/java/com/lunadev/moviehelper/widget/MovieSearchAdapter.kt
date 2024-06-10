package com.lunadev.moviehelper.widget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lunadev.moviehelper.R
import com.lunadev.moviehelper.model.MovieInfo

class MovieSearchAdapter : RecyclerView.Adapter<MovieSearchAdapter.MovieViewHolder>() {
    private var listener: OnItemSelected? = null
    private var data: List<MovieInfo.Data.Result?> = listOf()

    fun updateData(data: List<MovieInfo.Data.Result?>) {
        this.data = data
        notifyDataSetChanged()
    }

    fun addListener(listener: OnItemSelected) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.movie_element, parent, false)
        return MovieViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(data[position])
    }

    fun interface OnItemSelected {
        fun onItemSelected(movieResult: MovieInfo.Data.Result)
    }

    inner class MovieViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private val textViewMovieNm: TextView = v.findViewById(R.id.textViewMovieNm) // 제목으로 사용
        private val textViewOpenDt: TextView = v.findViewById(R.id.textViewOpenDt) // 개봉일로 사용
        private val textViewAudiAcc: TextView = v.findViewById(R.id.textViewAudiAcc) // 총 관객수로 사용
        private val imageViewPoster: ImageView = v.findViewById(R.id.imageView) // 포스터로 사용
        private val textViewRank: Button = v.findViewById(R.id.textViewMovieRank)
        private val textViewAudiCnt: TextView = v.findViewById(R.id.textViewAudiCnt) // 관람가로 사용

        init {
            v.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = data[position]
                    item?.let { listener?.onItemSelected(it) }
                }
            }
        }

        fun bind(movieResult: MovieInfo.Data.Result?) {
            textViewMovieNm.text = movieResult?.title?.replace(" !HS ", "")?.replace(" !HE ", "")
            textViewOpenDt.text = "개봉일: ${movieResult?.repRlsDate}"
            textViewAudiCnt.text = "${movieResult?.rating}"
            textViewAudiAcc.text = "장르: ${movieResult?.genre}"
            textViewRank.visibility = View.GONE

            val posterUrls = movieResult?.posters?.split("|")
            val firstPosterUrl = posterUrls?.firstOrNull() // 첫번째 포스터
            firstPosterUrl?.let {
                Glide.with(itemView.context)
                    .load(it)
                    .into(imageViewPoster)
            }
        }
    }
}
