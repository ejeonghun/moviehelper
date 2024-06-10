package com.lunadev.moviehelper.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lunadev.moviehelper.R
import com.lunadev.moviehelper.databinding.FragmentAppDescriptionBinding

class AppDescriptionFragment : Fragment() {
    private var _binding: FragmentAppDescriptionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppDescriptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.kmdbIcon.setOnClickListener {
            openUrl("https://www.kmdb.or.kr")
        }

        binding.kobisIcon.setOnClickListener {
            openUrl("http://www.kobis.or.kr")
        }

        binding.devIcon.setOnClickListener {
            openUrl("https://github.com/ejeonghun")
        }
    }

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }
}