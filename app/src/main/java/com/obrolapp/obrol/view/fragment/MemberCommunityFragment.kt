package com.obrolapp.obrol.view.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.obrolapp.obrol.R
import com.obrolapp.obrol.databinding.FragmentMemberCommunityBinding
import com.obrolapp.obrol.databinding.FragmentNotificationBinding
import com.obrolapp.obrol.model.user.HistoryCommunity
import com.obrolapp.obrol.model.user.Profile
import com.obrolapp.obrol.view.adapter.MembersAdapter
import com.obrolapp.obrol.viewmodel.CommunityViewModel

class MemberCommunityFragment : Fragment() {

    private lateinit var binding : FragmentMemberCommunityBinding
    private val communityVM : CommunityViewModel by viewModels()
    private lateinit var membersAdapter: MembersAdapter
    val listMembers = mutableListOf<Profile>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMemberCommunityBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loadLayout.visibility = View.VISIBLE

        membersAdapter = MembersAdapter(ArrayList())

        val getDataCommunity = arguments?.getSerializable("DATA_COMMUNITY")

        if(getDataCommunity != null){
            val dataCommunity = getDataCommunity as HistoryCommunity
            communityVM.getDataMembers(dataCommunity.codeCommunity)
        }

        setRvMembers()

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.i("SEARCH", newText.toString())
                filterDestination(newText, listMembers)
                return true
            }

        })


    }

    private fun filterDestination(text: String?, list: MutableList<Profile>) {
        val listTemporary = mutableListOf<Profile>()

        for(item in list){
            if(item.username.toLowerCase().contains(text!!.toLowerCase())){
                listTemporary.add(item)
            }
        }

        if(listTemporary.isNotEmpty()){
            membersAdapter.submitData(listTemporary as ArrayList<Profile>)
        }
    }



    private fun setRvMembers() {
        binding.rvMembers.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = membersAdapter
        }

        communityVM.responMembersCommunity.observe(viewLifecycleOwner){
            listMembers.clear()
            if(it != null){
                membersAdapter.submitData(it as ArrayList<Profile>)
                listMembers.addAll(it)
                binding.loadLayout.visibility = View.GONE
            }else{
                binding.loadLayout.visibility = View.GONE
            }
        }
    }


}