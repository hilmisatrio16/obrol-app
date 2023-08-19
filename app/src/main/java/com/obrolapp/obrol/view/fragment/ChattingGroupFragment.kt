package com.obrolapp.obrol.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.obrolapp.obrol.R
import com.obrolapp.obrol.databinding.FragmentChattingGroupBinding
import com.obrolapp.obrol.databinding.FragmentContentPublicBinding
import com.obrolapp.obrol.model.user.HistoryCommunity
import com.obrolapp.obrol.view.adapter.ChatCommunityAdapter
import com.obrolapp.obrol.viewmodel.UserViewModel

class ChattingGroupFragment : Fragment() {

    private lateinit var binding: FragmentChattingGroupBinding
    private lateinit var chatCommunityAdapter: ChatCommunityAdapter
    private val userVM : UserViewModel by viewModels()
    private var username : String = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChattingGroupBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomMenu)
        bottomNav.visibility = View.VISIBLE

        chatCommunityAdapter = ChatCommunityAdapter(ArrayList())

        binding.loadLayout.visibility = View.VISIBLE

        setRv()
    }

    private fun setRv() {
        binding.rvGroup.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = chatCommunityAdapter
        }

        userVM.retriveDataUser()

        userVM.dataProfile.observe(viewLifecycleOwner){
            if(it != null){
                if(it.community.size > 1){
                    val dataTemporary = mutableListOf<HistoryCommunity>()
                    for (i in it.community){
                        if(i.nameCommunity != "global"){
                            dataTemporary.add(i)
                        }
                    }
                    chatCommunityAdapter.submitData(dataTemporary as ArrayList<HistoryCommunity>)
                    username = it.profile.username
                    binding.loadLayout.visibility = View.GONE
                    binding.communityNotFound.visibility = View.GONE
                }else{
                    binding.loadLayout.visibility = View.GONE
                    binding.communityNotFound.visibility = View.VISIBLE
                }
            }else{
                binding.communityNotFound.visibility = View.VISIBLE
            }
        }

        chatCommunityAdapter.onClickItemGroup = {
            if(it.toString().isNotEmpty()){
                val dataBundle = Bundle().apply {
                    putSerializable("DATA_COMMUNITY", it)
                    putString("USERNAME", username)
                }
                findNavController().navigate(R.id.action_chattingGroupFragment_to_chatFragment, dataBundle)
            }
        }

    }

}