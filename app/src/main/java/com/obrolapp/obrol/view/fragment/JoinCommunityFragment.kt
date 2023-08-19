package com.obrolapp.obrol.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.obrolapp.obrol.R
import com.obrolapp.obrol.databinding.FragmentJoinCommunityBinding
import com.obrolapp.obrol.viewmodel.CommunityViewModel


class JoinCommunityFragment : Fragment() {

    private lateinit var binding : FragmentJoinCommunityBinding
    private val communityVM : CommunityViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentJoinCommunityBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomMenu)
        bottomNav.visibility = View.VISIBLE

        binding.btnCreateCommunity.setOnClickListener {
            findNavController().navigate(R.id.action_joinCommunityFragment_to_createCommunityFragment)
        }

        binding.btnJoin.setOnClickListener {
            binding.loadLayout.visibility = View.VISIBLE
            joinCommunity()
        }

        communityVM.responJoinCommunity.observe(viewLifecycleOwner){
            if(it != null){
                Toast.makeText(context, "Berhasil Join", Toast.LENGTH_SHORT).show()
                binding.loadLayout.visibility = View.GONE

                if(findNavController().currentDestination?.id != null){
                    findNavController().navigate(R.id.action_joinCommunityFragment_to_homeFragment2)
                }

            }else{
                Toast.makeText(context, "Code tidak ditemukan", Toast.LENGTH_SHORT).show()
                binding.loadLayout.visibility = View.GONE
            }
        }
    }

    private fun joinCommunity() {
        val code = binding.etCodeCommunity.text.toString()

        if(code.isNotEmpty() && binding.regulationOne.isChecked && binding.regulationTwo.isChecked && binding.regulationThree.isChecked) {
            communityVM.joinCommunity(code)
        }else{
            Toast.makeText(context, "kode tidak boleh kosong", Toast.LENGTH_SHORT).show()
            binding.loadLayout.visibility = View.GONE
        }
    }


}