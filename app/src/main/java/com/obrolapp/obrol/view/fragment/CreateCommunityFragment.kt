package com.obrolapp.obrol.view.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.obrolapp.obrol.R
import com.obrolapp.obrol.databinding.FragmentCreateCommunityBinding
import com.obrolapp.obrol.databinding.FragmentHomeBinding
import com.obrolapp.obrol.model.community.Community
import com.obrolapp.obrol.model.community.ProfileCommunity
import com.obrolapp.obrol.viewmodel.CommunityViewModel

class CreateCommunityFragment : Fragment() {

    private lateinit var binding : FragmentCreateCommunityBinding
    private val communityVM : CommunityViewModel by viewModels()
    private lateinit var imageUri : Uri

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCreateCommunityBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomMenu)
        bottomNav.visibility = View.GONE

        imageUri = Uri.EMPTY

        val arrayAdapterTitle = ArrayAdapter(requireContext(), R.layout.item_list_type_community, listOf("Sports", "Education","Entertainment"))

        binding.communityType.setAdapter(arrayAdapterTitle)
        binding.btnCreate.setOnClickListener {
            binding.loadLayout.visibility = View.VISIBLE
            createCommunity()
        }

        binding.uploadImageCommunity.setOnClickListener {
            uploadImageCommunity()
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        communityVM.responCreateCommunity.observe(viewLifecycleOwner){
            if(it != null){
                binding.loadLayout.visibility = View.GONE
                Toast.makeText(context, "Komunitas berhasil dibuat", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_createCommunityFragment_to_homeFragment2)
            }else{
                binding.loadLayout.visibility = View.GONE
                Toast.makeText(context,"Komunitas gagal dibuat", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadImageCommunity() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 100 && resultCode == Activity.RESULT_OK){
            imageUri = data?.data!!
            binding.imgCommunity.setImageURI(imageUri)
        }
    }

    private fun createCommunity() {
        val name = binding.etNameCommunity.text.toString()
        val description = binding.etDescription.text.toString()
        val type = binding.communityType.text.toString()
        val rules = binding.etRules.text.toString()

        //kurang kondisi

        if(name.isNotEmpty() && description.isNotEmpty() && type.isNotEmpty() && rules.isNotEmpty() && imageUri.toString().isNotEmpty() &&
                binding.regulationOne.isChecked && binding.regulationTwo.isChecked && binding.regulationThree.isChecked) {

            communityVM.uploadImage(imageUri, name, description, rules, type)

        }else{
            binding.loadLayout.visibility = View.GONE
            Toast.makeText(context, "Form tidak boleh kosong", Toast.LENGTH_SHORT).show()
        }
    }

}