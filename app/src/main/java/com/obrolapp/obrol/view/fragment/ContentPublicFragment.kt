package com.obrolapp.obrol.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.obrolapp.obrol.R
import com.obrolapp.obrol.databinding.FragmentContentPublicBinding
import com.obrolapp.obrol.databinding.FragmentCreateCommunityBinding
import com.obrolapp.obrol.model.post.ResponsePost
import com.obrolapp.obrol.view.adapter.ExplorAdapter
import com.obrolapp.obrol.viewmodel.PostViewModel


class ContentPublicFragment : Fragment() {

    private lateinit var binding : FragmentContentPublicBinding
    private val postVM : PostViewModel by viewModels()
    private lateinit var explorAdapter: ExplorAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentContentPublicBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomMenu)
        bottomNav.visibility = View.VISIBLE

        explorAdapter = ExplorAdapter(ArrayList())

        binding.loadLayout.visibility = View.VISIBLE

        setRvExplore()

        explorAdapter.onClickImage = {
            if(it.toString().isNotEmpty()){
                val putBundle = Bundle().apply {
                    putSerializable("DATA_POST", it)
                }
                findNavController().navigate(R.id.action_contentPublicFragment_to_detailExploreFragment, putBundle)
            }
        }
    }

    private fun setRvExplore() {
        binding.rvExploration.apply {
            layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
            adapter = explorAdapter
        }
        postVM.getDataExplor()

        postVM.responExplor.observe(viewLifecycleOwner){
            if(it.isNotEmpty()){
                explorAdapter.submitData(it as ArrayList<ResponsePost>)
                binding.loadLayout.visibility = View.GONE
            }else{
                binding.loadLayout.visibility = View.GONE
            }
        }
    }

}