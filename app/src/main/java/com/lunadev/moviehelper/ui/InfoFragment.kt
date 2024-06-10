package com.lunadev.moviehelper.ui

import ActorsAdapter
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lunadev.moviehelper.BuildConfig
import com.lunadev.moviehelper.MainViewModel
import com.lunadev.moviehelper.databinding.FragmentInfoBinding
import com.lunadev.moviehelper.model.MovieInfo
import com.lunadev.moviehelper.model.ReplyElement
import com.lunadev.moviehelper.model.ReplyWriteDTO
import com.lunadev.moviehelper.widget.CommentsAdapter
import com.lunadev.moviehelper.widget.ImageSlideshowAdapter
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

val supabaseUrl = BuildConfig.supabase_url
val supabaseKey = BuildConfig.supabase_key

// Supabase 설정
val supabase = createSupabaseClient(
    supabaseUrl = supabaseUrl,
    supabaseKey = supabaseKey
) {
    install(Postgrest)
}

class InfoFragment : Fragment() {
    private var _binding: FragmentInfoBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel
    private val commentsAdapter = CommentsAdapter()
    private val actorsAdapter = ActorsAdapter()
    private var docID = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        // 관찰자 삭제 (메모리 누수 방지) + 이전 데이터 초기화
        viewModel.selectedMovieInfo.removeObservers(viewLifecycleOwner)
        viewModel.selectedMovieResult.removeObservers(viewLifecycleOwner)

        Log.d("InfoFragment", "영화 이름 : ${viewModel.movieRouteValue.value}")
        // CommentsRecyclerView 설정
        binding.recyclerViewComments.apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = commentsAdapter
            Log.d("InfoFragment", "RecyclerView initialized")
        }

        // ActorsRecyclerView 설정
        binding.recyclerViewActors.apply {
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
            adapter = actorsAdapter
            Log.d("InfoFragment", "Actors RecyclerView initialized")
        }

        // MovieInfo 데이터를 관찰
        viewModel.selectedMovieInfo.observe(viewLifecycleOwner) { movieInfo ->
            movieInfo?.let { info ->
                // MovieInfo 데이터를 UI에 표시
                docID = info.data?.firstOrNull()?.result?.firstOrNull()?.dOCID!!
                fetchComments(docID) // 댓글 데이터 불러오기
                displayMovieInfo(info)
            }
        }


        // 댓글 작성
        binding.buttonSendComment.setOnClickListener {
            writeComment(binding.editTextComment.text.toString(), docID)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.imageViewStillCuts.adapter = null
        _binding = null
        commentsAdapter.clear()
        actorsAdapter.clear()

    }

    @SuppressLint("SetTextI18n")
    private fun displayMovieInfo(movieInfo: MovieInfo) {
        movieInfo.data?.firstOrNull()?.result?.let { info ->
            // 동영상 설정
            val videoViewStillCut = binding.videoViewStillCut
            var videoUrl = info.firstOrNull()?.vods?.vod?.firstOrNull()?.vodUrl
            Log.d("InfoFragment", "Video URL: $videoUrl")

            if (!videoUrl.isNullOrEmpty()) {
                // 만약 동영상 URL이 있다면 스틸샷 imageView는 가림
                binding.imageViewStillCuts.visibility = View.INVISIBLE
                // CORS 문제 떄문에 https://www.kmdb.or.kr/trailer/trailerPlayPop?pFileNm=MK060579_P02.mp4 -> https://www.kmdb.or.kr/trailer/play/MK060579_P02.mp4 형식으로 변경
                videoUrl = videoUrl.replace("/trailerPlayPop?pFileNm=", "/play/")
                val uri = Uri.parse(videoUrl)
                val mediaController = android.widget.MediaController(requireContext())
                mediaController.setAnchorView(videoViewStillCut)

                videoViewStillCut.apply {
                    setMediaController(mediaController)
                    setVideoURI(uri)
                    setOnPreparedListener { mediaPlayer ->
                        mediaPlayer.start()
                        // Optionally set video loop
                        mediaPlayer.isLooping = true
                    }
                    setOnErrorListener { _, what, extra ->
                        Log.e("VideoView", "Error: $what, Extra: $extra")
                        true
                    }
                }
            } else {
                // 만약 동영상이 없다면 동영상 UI는 가림
                binding.videoViewStillCut.visibility = View.INVISIBLE
                Log.d("InfoFragment", "예고편이 없음")
            }

            // 영화 포스터
            val posterUrls = info.firstOrNull()?.posters?.split("|")
            val firstPosterUrl = posterUrls?.firstOrNull()
            firstPosterUrl?.let {
                Glide.with(requireContext())
                    .load(firstPosterUrl)
                    .into(binding.imageViewPoster)
            }

            val stillCuts = info.firstOrNull()?.stlls?.split("|") ?: emptyList()
            Log.d("InfoFragment", "Still Cuts: $stillCuts")
            // 이미지 슬라이드 쇼 어댑터 설정
            val viewPager = binding.imageViewStillCuts
            val adapter = ImageSlideshowAdapter(requireContext(), stillCuts)
            viewPager.adapter = adapter

            // 5초마다 자동으로 넘어가도록 설정
            val handler = Handler(Looper.getMainLooper())
            val delayMillis = 5000L // 5 seconds
            val imageRunnable = object : Runnable {
                override fun run() {
                    val currentItem = viewPager.currentItem
                    viewPager.setCurrentItem((currentItem + 1) % stillCuts.size, true)
                    handler.postDelayed(this, delayMillis)
                }
            }
            handler.postDelayed(imageRunnable, delayMillis)


//            val stillCuts = info.firstOrNull()?.stlls?.split("|")
//            val firstStillUrl = stillCuts?.firstOrNull()
//            firstStillUrl.let { stillCut ->
//                Glide.with(requireContext())
//                    .load(stillCut)
//                    .into(binding.imageViewStillCut)
//            }

            binding.textViewReleaseDate.text = "개봉일 : ${info.firstOrNull()?.repRlsDate}" // 개봉일
            binding.textViewDirector.text = "감독 ${info.firstOrNull()?.directors?.director?.firstOrNull()?.directorNm}" // 감독
            binding.textViewTitle.text = info.firstOrNull()?.title?.replace(" !HE ", "")?.replace(" !HS ", "") // 영화 제목
            binding.textViewOriginalTitle.text = if(info.firstOrNull()?.titleOrg != "") "${info.firstOrNull()?.titleOrg}" else "${info.firstOrNull()?.titleEng}" // 영어제목 혹은 해당 국가 제목
            Log.d("InfoFragment", "영화 제목: ${info.firstOrNull()?.title}, ${info.firstOrNull()?.titleOrg}, ${info.firstOrNull()?.titleEng}")
            binding.textViewTotalVisitors.text = "누적 관객 수 : ${viewModel.selectedMovieElement.value?.audiAcc}명" // 누적 관객 수
            binding.genre.text = info.firstOrNull()?.genre // 장르
            binding.textViewRating.text = "${info.firstOrNull()?.rating}" // 등급
            binding.textViewSummary.text = info.firstOrNull()?.plots?.plot?.firstOrNull()?.plotText // 줄거리
            binding.textViewRuntime.text = "총 상영시간 : ${info.firstOrNull()?.runtime}분" // 총 러닝 타임
            val marqueeTextView: TextView = binding.textViewTitle // 글씨 길면 움직이게 설정
            marqueeTextView.isSelected = true // 포커스

            binding.textViewCompany.text = "제작사 : ${info.firstOrNull()?.company}" // 제작사

            binding.buttonMoreDetail.setOnClickListener {// 자세히보기 버튼 클릭 시 kmdb 페이지로 이동
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(info.firstOrNull()?.kmdbUrl)
                startActivity(intent)
            }

            val actors = info.firstOrNull()?.actors?.actor ?: emptyList()
            actorsAdapter.submitList(actors)
        }
    }

    private fun fetchComments(docID :String) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                commentsAdapter.clear()
                Log.d("InfoFragment", "영화 고유 ID: ${docID}")
                val response = supabase
                    .from("reply")
                    .select(Columns.ALL) {
                        filter {
                            eq("docId", docID)
                        }
                    }


                if (response.data.isNotEmpty()) {
                    Log.d("InfoFragment", "Response Data: ${response.data}")
                    val type = object : TypeToken<List<ReplyElement>>() {}.type
                    val comments: List<ReplyElement> = Gson().fromJson(response.data, type)

                    withContext(Dispatchers.Main) {
                        Log.d("InfoFragment", "댓글목록: $comments")
                        commentsAdapter.submitList(comments)
                        Log.d("InfoFragment", "어댑터 리스트: ${commentsAdapter.currentList}")
                    }
                } else {
                    Log.e("InfoFragment", "API 통신 실패")
                }
            } catch (e: Exception) {
                Log.e("InfoFragment", "Error: $e")
            }
        }
    }


    private fun writeComment(content:String, docID: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                Log.d("InfoFragment", "영화 고유 ID: ${docID}")
                val response = supabase
                    .from("reply")
                    .insert(ReplyWriteDTO(
                        content = content,
                        docId = docID,
                        // 영화 고유번호로 댓글을 구분
                    ))

                if (response.data.isNotEmpty()) {
                    Log.d("InfoFragment", "댓글 작성 성공")
                    binding.editTextComment.text.clear() // 댓글 작성 후 EditText 초기화
                    fetchComments(docID) // 새로 DB에 데이터 요청
                } else {
                    Log.e("InfoFragment", "API 통신 실패")
                }
            } catch (e: Exception) {
                Log.e("InfoFragment", "Error: $e")
            }
        }
    }
}