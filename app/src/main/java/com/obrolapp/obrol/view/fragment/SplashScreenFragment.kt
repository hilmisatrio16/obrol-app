package com.obrolapp.obrol.view.fragment

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.obrolapp.obrol.R
import com.obrolapp.obrol.data.UserPrefs
import com.obrolapp.obrol.databinding.FragmentSplashScreenBinding

class SplashScreenFragment : Fragment() {

    private lateinit var binding : FragmentSplashScreenBinding
    private lateinit var userPrefs : UserPrefs
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSplashScreenBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userPrefs = UserPrefs(requireContext().applicationContext)
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomMenu)
        bottomNav.visibility = View.GONE
        Handler().postDelayed({
            if(userPrefs.isAlreadyLogin()){
                findNavController().navigate(R.id.action_splashScreenFragment_to_homeFragment2)
            }else{
                findNavController().navigate(R.id.action_splashScreenFragment_to_welcomeFragment)
            }
        }, 5000)
    }

}