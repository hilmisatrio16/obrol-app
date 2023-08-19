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
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.obrolapp.obrol.R
import com.obrolapp.obrol.databinding.FragmentRegisterBinding
import com.obrolapp.obrol.model.user.Profile
import com.obrolapp.obrol.viewmodel.UserViewModel
import kotlin.math.sign


class RegisterFragment : Fragment() {

    private lateinit var binding : FragmentRegisterBinding
    private lateinit var imageUri : Uri
    private val userViewModel : UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomMenu)
        bottomNav.visibility = View.GONE

        imageUri = Uri.EMPTY

        binding.btnRegister.setOnClickListener {
            binding.loadLayout.visibility = View.VISIBLE
            signUp()
        }

        binding.uploadImageProfile.setOnClickListener {
            uploadImageProfile()
        }

        userViewModel.responSignUp.observe(viewLifecycleOwner){
            if(it != null){
                Toast.makeText(context, "Pendaftaran Berhasil", Toast.LENGTH_SHORT).show()
                if(findNavController().currentDestination?.id != null){
                    findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                }
                binding.loadLayout.visibility = View.GONE
            }else{
                Toast.makeText(context, "Pendaftaran Gagal", Toast.LENGTH_SHORT).show()
                binding.loadLayout.visibility = View.GONE
            }
        }

        binding.btnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    private fun uploadImageProfile() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 100 && resultCode == Activity.RESULT_OK){
            imageUri = data?.data!!
            binding.imgProfile.setImageURI(imageUri)
        }
    }

    private fun signUp() {
        val fullName = binding.etFullName.text.toString()
        val username = binding.etUsername.text.toString()
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        val image = ""
        if(fullName.isNotEmpty() && username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && imageUri.toString().isNotEmpty()){
            userViewModel.signUpAction(Profile(fullName,username,email, password,image), imageUri)

//            userViewModel.responSignUp.observe(viewLifecycleOwner){
//                if(it != null){
//                    Toast.makeText(context, "Pendaftaran Berhasil", Toast.LENGTH_SHORT).show()
//                    if(findNavController().currentDestination?.id != null){
//                        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
//                    }
//                    binding.loadLayout.visibility = View.GONE
//                }else{
//                    Toast.makeText(context, "Pendaftaran Gagal", Toast.LENGTH_SHORT).show()
//                    binding.loadLayout.visibility = View.GONE
//                }
//            }
        }else{
            Toast.makeText(context, "Form tidak boleh kosong", Toast.LENGTH_SHORT).show()
            binding.loadLayout.visibility = View.GONE
        }

    }


}