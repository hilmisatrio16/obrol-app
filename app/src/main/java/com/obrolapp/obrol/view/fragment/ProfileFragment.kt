package com.obrolapp.obrol.view.fragment

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.MenuRes
import androidx.annotation.RawRes
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.storage.FirebaseStorage
import com.obrolapp.obrol.R
import com.obrolapp.obrol.data.UserPrefs
import com.obrolapp.obrol.databinding.FragmentProfileBinding
import com.obrolapp.obrol.databinding.FragmentRegisterBinding
import com.obrolapp.obrol.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import java.io.File

class ProfileFragment : Fragment() {

    private lateinit var binding : FragmentProfileBinding
    private val userViewModel : UserViewModel by viewModels()
    private lateinit var userPref : UserPrefs
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomMenu)
        bottomNav.visibility = View.VISIBLE

        userPref = UserPrefs(requireContext().applicationContext)
        binding.expandMoreMenu.setOnClickListener {
            val v = binding.tvUsername
            showMenus(v, R.menu.profile_menu)
        }
        binding.loadLayout?.visibility = View.VISIBLE
        showDataProfile()
    }

    @SuppressLint("SetTextI18n")
    private fun showDataProfile() {

        userViewModel.retriveDataUser()
        userViewModel.dataProfile.observe(viewLifecycleOwner){
            if(it != null){
                binding.tvUsername.text = "Halo, ${it.profile.username}"
                binding.etUsername.setText(it.profile.username)
                binding.etfullName.setText(it.profile.fullName)

                Log.i("IMAGE PROFILE", it.profile.imageProfile)
                val storageRef = FirebaseStorage.getInstance().reference.child("images/${it.profile.imageProfile}")
                storageRef.downloadUrl.addOnSuccessListener {uri->
                    val imageUrl = uri.toString()

                    if(uri != null){
                        Glide.with(this)
                            .load(imageUrl)
                            .circleCrop()
                            .into(binding.imageProfile)
                    }

                }
                binding.loadLayout?.visibility = View.GONE
            }else{
                binding.loadLayout?.visibility = View.GONE
            }
        }
    }

    private fun showMenus(v: View, @MenuRes menuRes: Int) {
        val popup = PopupMenu(context!!, v)
        popup.menuInflater.inflate(menuRes, popup.menu)
        popup.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.logOut -> logOut()
            }
            true
        }
        popup.setOnDismissListener {
           popup.dismiss()
        }
        // Show the popup menu.
        popup.show()
    }

    private fun logOut() {
        binding.loadLayout?.visibility = View.VISIBLE
        userViewModel.signOutAction()

        userViewModel.responSignOut.observe(viewLifecycleOwner){
            if(it != null){
                lifecycleScope.launch {
                    userPref.clear()

                    if(!userPref.isAlreadyLogin()){
                        Toast.makeText(context, "Berhasil keluar", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_profileFragment2_to_loginFragment)
                        binding.loadLayout?.visibility = View.GONE
                    }else{
                        Toast.makeText(context, "gagal keluar", Toast.LENGTH_SHORT).show()
                        binding.loadLayout?.visibility = View.GONE
                    }
                }

            }else{
                Toast.makeText(context, "Gagal keluar", Toast.LENGTH_SHORT).show()
                binding.loadLayout?.visibility = View.GONE
            }
        }
    }



}