package com.lunadev.moviehelper.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.lunadev.moviehelper.MainViewModel
import com.lunadev.moviehelper.R
import com.lunadev.moviehelper.databinding.FragmentListBinding
import com.lunadev.moviehelper.model.MovieInfo
import com.lunadev.moviehelper.widget.ListAdapter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ListFragment : Fragment() {
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private val adapter = ListAdapter()
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.recyclerView.adapter = adapter

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.selectedDate.observe(viewLifecycleOwner) { dateString ->
            dateString?.let {
                val inputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
                val date = LocalDate.parse(it, inputFormatter)
                val outputFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")
                val formattedDate = date.format(outputFormatter)
                binding.textViewDate.text = "$formattedDate 박스오피스 순위"
                viewModel.getRank(it)
            }
        }

        viewModel.dailyBoxOfficeList.observe(viewLifecycleOwner) { movieElementAndPosters ->
            // movieElementAndPosters에서 각각의 MovieElementAndPoster 객체에서 movieElement를 추출하여 목록 생성
            val movieElements = movieElementAndPosters.map { it.movieElement }
            adapter.updateData(movieElementAndPosters)
            Log.d("ListFragment", "movies: ${movieElements.size}")
        }

        adapter.addListener { movieElementAndInfo ->
            viewModel.movieRouteValue.value = movieElementAndInfo.movieElement.movieNm
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
                viewModel.setSelectedMovieInfo(latestMovieInfo)
                viewModel.setSelectedMovieElement(movieElementAndInfo.movieElement)
                findNavController().navigate(R.id.action_listFragment_to_infoFragment)
            }
        }


    }