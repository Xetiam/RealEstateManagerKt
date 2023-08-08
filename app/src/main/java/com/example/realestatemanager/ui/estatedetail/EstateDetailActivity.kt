package com.example.realestatemanager.ui.estatedetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.realestatemanager.databinding.FragmentEstateDetailBinding

class EstateDetailFragment : Fragment() {
    private val viewModel: EstateDetailViewModel by viewModels()
    private val binding: FragmentEstateDetailBinding by lazy {
        FragmentEstateDetailBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        return binding.root
    }
}