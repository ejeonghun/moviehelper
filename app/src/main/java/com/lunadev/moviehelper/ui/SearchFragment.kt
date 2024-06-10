package com.lunadev.moviehelper.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.lunadev.moviehelper.MainViewModel
import com.lunadev.moviehelper.R
import com.lunadev.moviehelper.databinding.FragmentSearchBinding
import com.lunadev.moviehelper.model.MovieInfo
import com.lunadev.moviehelper.widget.MovieSearchAdapter

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel
    private val adapter = MovieSearchAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonSearch.setOnClickListener {
            val query = binding.editTextSearch.text.toString()
            viewModel.getMovieSearchs(query) { movieResults ->
                adapter.updateData(movieResults)
            }

            adapter.addListener { movieResult ->
                viewModel.setSelectedMovieResult(listOf(movieResult))
                findNavController().navigate(R.id.action_searchFragment_to_searchInfoFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
