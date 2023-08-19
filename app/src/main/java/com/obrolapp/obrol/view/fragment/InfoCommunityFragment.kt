package com.obrolapp.obrol.view.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.obrolapp.obrol.R
import com.obrolapp.obrol.databinding.FragmentInfoCommunityBinding
import com.obrolapp.obrol.databinding.FragmentMemberCommunityBinding
import com.obrolapp.obrol.model.user.HistoryCommunity
import com.obrolapp.obrol.viewmodel.CommunityViewModel


class InfoCommunityFragment : Fragment() {

    private lateinit var binding : FragmentInfoCommunityBinding
    private val communityVM : CommunityViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentInfoCommunityBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val getDataCommunity = arguments?.getSerializable("DATA_COMMUNITY")

        binding.loadLayout.visibility = View.VISIBLE

        if(getDataCommunity != null){
            val dataCommunity = getDataCommunity as HistoryCommunity
            communityVM.getDataCommunity(dataCommunity.codeCommunity)
        }

        communityVM.responDataCommunity.observe(viewLifecycleOwner){
            if(it != null){
                val storageReference = FirebaseStorage.getInstance().reference.child("community/${it.image}").downloadUrl

                storageReference.addOnSuccessListener {image->
                    try {
                        Glide.with(requireActivity())
                            .load(image)
                            .circleCrop()
                            .into(binding.imageProfileCommunity)
                    }catch (e : java.lang.Exception){
                        Log.e("Error Glide", "error")
                    }

                }
                binding.community.text = it.name
                val dataCommunity = getDataCommunity as HistoryCommunity
                binding.tvCodeCommunity.text = dataCommunity.codeCommunity
                binding.tvDescription.text = it.description
                binding.tvRules.text = it.rules
                binding.loadLayout.visibility = View.GONE
            }else{
                binding.loadLayout.visibility = View.GONE
            }
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnMembers.setOnClickListener {
            if(getDataCommunity != null) {
                val dataCommunity = getDataCommunity as HistoryCommunity

                val dataBundle = Bundle().apply {
                    putSerializable("DATA_COMMUNITY", dataCommunity)
                }
                if(findNavController().currentDestination?.id != null){
                    findNavController().navigate(R.id.action_infoCommunityFragment_to_memberCommunityFragment, dataBundle)
                }

            }
        }
    }


}