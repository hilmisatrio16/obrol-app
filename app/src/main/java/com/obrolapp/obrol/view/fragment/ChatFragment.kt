package com.obrolapp.obrol.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.obrolapp.obrol.R
import com.obrolapp.obrol.databinding.FragmentChatBinding
import com.obrolapp.obrol.model.community.Message
import com.obrolapp.obrol.model.user.HistoryCommunity
import com.obrolapp.obrol.view.adapter.MessageAdapter
import com.obrolapp.obrol.viewmodel.CommunityViewModel


class ChatFragment : Fragment() {
    private lateinit var binding : FragmentChatBinding
    private val communityVM : CommunityViewModel by viewModels()
    private lateinit var messageAdapter: MessageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomMenu)
        bottomNav.visibility = View.GONE

        val getDataCommunity = arguments?.getSerializable("DATA_COMMUNITY")
        val getUsername = arguments?.getString("USERNAME")

        messageAdapter = MessageAdapter(ArrayList())

        binding.loadLayout.visibility = View.VISIBLE

        if(getDataCommunity != null){
            val dataCommunity = getDataCommunity as HistoryCommunity
            communityVM.getDataMessage(dataCommunity.codeCommunity)
            binding.community.text = dataCommunity.nameCommunity

        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSend.setOnClickListener {
            if(getDataCommunity != null && getUsername != null){
                val dataHistoryCommunity = getDataCommunity as HistoryCommunity
                sentMessage(dataHistoryCommunity, getUsername.toString())
            }
        }

        communityVM.responSendMessage.observe(viewLifecycleOwner){
            if(it != null){
                binding.etMessage.setText("")
                if(getDataCommunity != null) {
                    val dataCommunity = getDataCommunity as HistoryCommunity
                    communityVM.getDataMessage(dataCommunity.codeCommunity)
                }
                Toast.makeText(context, "Berhasil terkirim", Toast.LENGTH_SHORT).show()
            }
        }

        communityVM.responDataMessage.observe(viewLifecycleOwner){
            if(it.isNotEmpty()){
                binding.rvChat.apply {
                    layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    adapter = messageAdapter
                }

                messageAdapter.submitData(it as ArrayList<Message>)

                binding.loadLayout.visibility = View.GONE
                binding.messageEmpty.visibility = View.GONE
            }else{
                binding.loadLayout.visibility = View.GONE
                binding.messageEmpty.visibility = View.VISIBLE
            }
        }

        binding.community.setOnClickListener {
            if(getDataCommunity != null) {
                val dataCommunity = getDataCommunity as HistoryCommunity

                val dataBundle = Bundle().apply {
                    putSerializable("DATA_COMMUNITY", dataCommunity)
                }

                findNavController().navigate(R.id.action_chatFragment_to_infoCommunityFragment, dataBundle)
            }

        }
    }

    private fun sentMessage(data: HistoryCommunity, username: String) {
        val message = binding.etMessage.text.toString()
        if(message.isNotEmpty()){
            communityVM.sentMessage(username, message, data.codeCommunity)
        }else{
            Toast.makeText(context, "Pesan tidak boleh kosong!", Toast.LENGTH_SHORT).show()
        }
    }


}