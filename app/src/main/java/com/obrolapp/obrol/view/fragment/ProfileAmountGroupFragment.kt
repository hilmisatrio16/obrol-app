package com.obrolapp.obrol.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.obrolapp.obrol.R
import com.obrolapp.obrol.databinding.FragmentProfileAmountGroupBinding
import com.obrolapp.obrol.databinding.FragmentProfileBinding


class ProfileAmountGroupFragment : Fragment() {

    private lateinit var binding : FragmentProfileAmountGroupBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileAmountGroupBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


}