package com.obrolapp.obrol.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.obrolapp.obrol.R
import com.obrolapp.obrol.databinding.FragmentSearchCommunityBinding
import com.obrolapp.obrol.databinding.FragmentWelcomeBinding

class SearchCommunityFragment : Fragment() {

    private lateinit var binding : FragmentSearchCommunityBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSearchCommunityBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

}