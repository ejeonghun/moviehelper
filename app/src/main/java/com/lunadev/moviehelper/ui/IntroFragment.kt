package com.lunadev.moviehelper.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.lunadev.moviehelper.MainViewModel
import com.lunadev.moviehelper.R
import com.lunadev.moviehelper.databinding.FragmentIntroBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

class IntroFragment : Fragment() {
    private var _binding:FragmentIntroBinding?=null
    private val binding get()= _binding!!
    private lateinit var viewModel: MainViewModel

    private var selectedDate: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIntroBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java] // MainViewModel 객체 생성
        val yesterday = LocalDateTime.now().minusDays(1)
        selectedDate = yesterday.format(DateTimeFormatter.ofPattern("yyyyMMdd")) // 어제 날짜를 yyyymmdd 형식으로 미리 저장시켜놓음 -> 선택한 날짜가 없을 때 기본값으로 사용

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val yesterdayEnd = calendar.timeInMillis
        binding.calendarView.maxDate = yesterdayEnd // API는 당일의 순위를 못 불러옴 어제날짜까지 선택 가능하도록

        binding.calendarView.setOnDateChangeListener { view, year, month, dayOfMonth -> // 날짜를 선택하면
            Log.d("IntroFragment", "날짜 선택 : $year${String.format("%02d",month+1)}${String.format("%02d",dayOfMonth)}") // yyyymmdd 형식
            selectedDate = "$year${String.format("%02d",month+1)}${String.format("%02d",dayOfMonth)}" // yyyymmdd 형식으로 String 저장
        }
        binding.buttonStart.setOnClickListener {
            Log.d("IntroFragment", "박스오피스 API 요청")
            viewModel.selectedDate.value = selectedDate // 선택한 날짜를 MainViewModel에 저장
            findNavController().navigate(R.id.action_introFragment_to_listFragment) // ListFragment로 이동
        }

        binding.searchIcon.setOnClickListener {
            findNavController().navigate(R.id.action_introFragment_to_searchFragment) // SearchFragment로 이동
        }

        binding.infoIcon.setOnClickListener {
            findNavController().navigate(R.id.action_introFragment_to_appDescriptionFragment) // AppDescriptionFragment로 이동
        }
    }
}