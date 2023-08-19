package com.obrolapp.obrol.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.obrolapp.obrol.R
import com.obrolapp.obrol.data.UserPrefs
import com.obrolapp.obrol.databinding.FragmentLoginBinding
import com.obrolapp.obrol.databinding.FragmentMemberCommunityBinding
import com.obrolapp.obrol.viewmodel.UserViewModel
import kotlinx.coroutines.launch


class LoginFragment : Fragment() {

    private lateinit var binding : FragmentLoginBinding
    private val userViewModel : UserViewModel by viewModels()
    private lateinit var userDS : UserPrefs
    private val fireAuth = Firebase.auth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userDS = UserPrefs(requireContext().applicationContext)
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomMenu)
        bottomNav.visibility = View.GONE

        binding.btnLogin.setOnClickListener {
            binding.loadLayout.visibility = View.VISIBLE
            signIn()
        }

        binding.btnRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.btnBack.setOnClickListener {
            activity?.finish()
        }

        userDS.isLoggin().asLiveData().observe(viewLifecycleOwner){
            if(it){
                if(findNavController().currentDestination?.id != null){
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment2)
                    binding.loadLayout.visibility = View.GONE
                }else{
                    binding.loadLayout.visibility = View.GONE
                }

            }else{
                binding.loadLayout.visibility = View.GONE
            }
        }
    }

    private fun signIn() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        if(email.isNotEmpty() && password.isNotEmpty()){
            userViewModel.signInAction(email, password)

            userViewModel.responSignIn.observe(viewLifecycleOwner){
                if(it != null){
                    Toast.makeText(context, "Berhasil masuk", Toast.LENGTH_SHORT).show()
                    insertDataUser()
                }else{
                    Toast.makeText(context, "Gagal masuk", Toast.LENGTH_SHORT).show()
                    binding.loadLayout.visibility = View.GONE
                }
            }
        }else{
            Toast.makeText(context, "form tidak boleh kosong!", Toast.LENGTH_SHORT).show()
            binding.loadLayout.visibility = View.GONE
        }

    }

    private fun insertDataUser() {
        lifecycleScope.launch {
            userDS.saveUser(fireAuth.currentUser!!.uid)
        }
    }


}