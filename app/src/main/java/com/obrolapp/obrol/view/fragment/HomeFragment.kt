package com.obrolapp.obrol.view.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.obrolapp.obrol.R
import com.obrolapp.obrol.databinding.DialogCommentBinding
import com.obrolapp.obrol.databinding.FragmentHomeBinding
import com.obrolapp.obrol.model.post.Comments
import com.obrolapp.obrol.model.post.Post
import com.obrolapp.obrol.model.post.ResponsePost
import com.obrolapp.obrol.view.adapter.CommentsAdapter
import com.obrolapp.obrol.view.adapter.PostAdapter
import com.obrolapp.obrol.viewmodel.PostViewModel
import com.obrolapp.obrol.viewmodel.UserViewModel

class HomeFragment : Fragment() {

    private lateinit var binding : FragmentHomeBinding
    private val postVM : PostViewModel by viewModels()
    private val userVM : UserViewModel by viewModels()
    private lateinit var commentsAdapter : CommentsAdapter
    private lateinit var postAdapter: PostAdapter

    private var dialogIsShowing = false
    private var username = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomMenu)
        bottomNav.visibility = View.VISIBLE

        binding.btnPost.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment2_to_postContentFragment)
        }

        binding.loadLayout.visibility = View.VISIBLE

        setRv()

        postAdapter.onClickComment = {
            dialogIsShowing = if(!dialogIsShowing){
                showComments(it)
                postVM.getDataComment(it.documentId)
                true
            }else{
                false
            }
        }

        postVM.responDataComments.observe(viewLifecycleOwner){
            if(it.isNotEmpty()){
                Log.i("comment", "data masuk")
                commentsAdapter.submitData(it as ArrayList<Comments>)
            }
        }

        commentsAdapter = CommentsAdapter(ArrayList())

    }

    private fun showComments(post: ResponsePost) {
        val dialog = BottomSheetDialog(requireContext())

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_comment)
        val bindingDialog = DialogCommentBinding.inflate(layoutInflater)
        dialog.setContentView(bindingDialog.root)

        dialogIsShowing = dialog.isShowing

        bindingDialog.btnSend.setOnClickListener {
            val comment = bindingDialog.etComment.text.toString()
            if(comment.isNotEmpty()){
                postVM.sendComment(post.documentId, username, comment)
                bindingDialog.etComment.setText("")
                postVM.getDataComment(post.documentId)
                Log.i("comment", "not empty")
            }else{
                Log.i("comment", "empty")
            }
        }
        bindingDialog.rvComment.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = commentsAdapter
        }



        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)




    }

    @SuppressLint("SetTextI18n")
    private fun setRv() {

        postAdapter = PostAdapter(ArrayList())

        binding.rvPost.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = postAdapter
        }
        userVM.retriveDataUser()

        userVM.dataProfile.observe(viewLifecycleOwner){
            if(it != null){
                if(it.community.size > 1){
                    val data : MutableList<String> = mutableListOf()
                    for(i in it.community){
                        data.add(i.codeCommunity)
                    }
                    username = it.profile.username
                    Log.i("INFO COMMUNITY USER", data.toString())
                    postVM.getDataPost(data)
                    binding.communityNotFound.visibility = View.GONE
                }else{
                    binding.loadLayout.visibility = View.GONE
                    binding.communityNotFound.visibility = View.VISIBLE
                }
            }else{
                binding.loadLayout.visibility = View.GONE
            }
        }


        postVM.dataPost.observe(viewLifecycleOwner){
            if(it.isNotEmpty()){
                Log.i("INFO DATA POST", it.toString())
                postAdapter.submitData(it as ArrayList<ResponsePost>)
                binding.loadLayout.visibility = View.GONE
                binding.communityNotFound.visibility = View.GONE
            }else{
                binding.loadLayout.visibility = View.GONE
                binding.communityNotFound.visibility = View.VISIBLE
            }
        }

    }

}