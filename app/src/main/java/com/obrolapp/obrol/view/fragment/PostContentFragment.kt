package com.obrolapp.obrol.view.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.obrolapp.obrol.R
import com.obrolapp.obrol.databinding.FragmentPostContentBinding
import com.obrolapp.obrol.model.post.Post
import com.obrolapp.obrol.model.user.HistoryCommunity
import com.obrolapp.obrol.viewmodel.CommunityViewModel
import com.obrolapp.obrol.viewmodel.PostViewModel
import com.obrolapp.obrol.viewmodel.UserViewModel

class PostContentFragment : Fragment() {

    private lateinit var binding : FragmentPostContentBinding
    private val postVM : PostViewModel by viewModels()
    private val userVM : UserViewModel by viewModels()
    private lateinit var imageUri : Uri

    private val firebaseAuth = Firebase.auth

    private val listIdCommunity = mutableListOf<String>()
    private var communityPosition = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPostContentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomMenu)
        bottomNav.visibility = View.GONE

        setDropDown()

        imageUri = Uri.EMPTY

        binding.btnPost.setOnClickListener {
            binding.loadLayout.visibility = View.VISIBLE
            postContent()
        }

        binding.uploadImagePost.setOnClickListener {
            uploadImagePost()
        }

        binding.community.setOnItemClickListener{parent, view, position, id ->
            communityPosition = position
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }



        postVM.responPost.observe(viewLifecycleOwner){
            if(it != null){
                Toast.makeText(context, "Postingan Berhasil Dibuat", Toast.LENGTH_SHORT).show()
                binding.loadLayout.visibility = View.GONE
                findNavController().navigate(R.id.action_postContentFragment_to_homeFragment2)
            }else{
                Toast.makeText(context, "Postingan gagal dibuat", Toast.LENGTH_SHORT).show()
                binding.loadLayout.visibility = View.GONE
            }
        }
    }

    private fun setDropDown() {

        userVM.retriveDataUser()
        userVM.dataProfile.observe(viewLifecycleOwner){
            if(it != null){

                val data : MutableList<String> = mutableListOf()

                for(i in it.community){
                    data.add(i.nameCommunity)
                    listIdCommunity.add(i.codeCommunity)
                }
                Log.i("INFO DROPDOWN", data.toString())

                val arrayAdapterTitle = ArrayAdapter(requireContext(), R.layout.item_list_type_community, data)
                binding.community.setText(data[0])
                binding.community.setAdapter(arrayAdapterTitle)
            }
        }


    }

    private fun uploadImagePost() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 100 && resultCode == Activity.RESULT_OK){
            imageUri = data?.data!!
            binding.imgPost.setImageURI(imageUri)
        }
    }

    private fun postContent() {

        val content = binding.etPostContent.text.toString()
        val idCommunity = listIdCommunity[communityPosition]

        if(content.isNotEmpty() && imageUri.toString().isNotEmpty() && binding.regulationOne.isChecked && binding.regulationTwo.isChecked){
            userVM.retriveDataUser()
            //double post (data double)
            userVM.dataProfile.observe(viewLifecycleOwner){
                if(it != null){
                    val post = Post(idCommunity, firebaseAuth.currentUser!!.uid, it.profile.username, content)
                    postVM.uploadImage(post, imageUri)
                    userVM.dataProfile.removeObservers(viewLifecycleOwner)
                }
            }
        }else{
            Toast.makeText(context, "form tidak boleh kosong!", Toast.LENGTH_SHORT).show()
            binding.loadLayout.visibility = View.GONE
        }

    }


}