package com.obrolapp.obrol.view.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.storage.FirebaseStorage
import com.obrolapp.obrol.R
import com.obrolapp.obrol.databinding.DialogCommentBinding
import com.obrolapp.obrol.databinding.FragmentDetailExploreBinding
import com.obrolapp.obrol.model.post.Comments
import com.obrolapp.obrol.model.post.ResponsePost
import com.obrolapp.obrol.view.adapter.CommentsAdapter
import com.obrolapp.obrol.viewmodel.PostViewModel
import com.obrolapp.obrol.viewmodel.UserViewModel

class DetailExploreFragment : Fragment() {

    private lateinit var binding : FragmentDetailExploreBinding
    private val userVM : UserViewModel by viewModels()
    private val postVM : PostViewModel by viewModels()

    private lateinit var commentsAdapter : CommentsAdapter

    private var dialogIsShowing = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDetailExploreBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomMenu)
        bottomNav.visibility = View.GONE

        val getDataBundle = arguments?.getSerializable("DATA_POST")

        if(getDataBundle != null){
            setAppearancePost(getDataBundle as ResponsePost)
        }

        binding.btnComment.setOnClickListener {
            dialogIsShowing = if(getDataBundle != null && !dialogIsShowing) {
                val data = getDataBundle as ResponsePost
                showCommentDialog(data)
                postVM.getDataCommentExplor(data.documentId)
                true
            }else{
                false
            }
        }

        binding.btnCommentIcon.setOnClickListener {
            dialogIsShowing = if(getDataBundle != null && !dialogIsShowing) {
                val data = getDataBundle as ResponsePost
                showCommentDialog(data)
                postVM.getDataCommentExplor(data.documentId)
                true
            }else{
                false
            }
        }

        commentsAdapter = CommentsAdapter(ArrayList())

        postVM.responDataCommentsExplore.observe(viewLifecycleOwner){
            if(it.isNotEmpty()){
                Log.i("comment", "data masuk")
                commentsAdapter.submitData(it as ArrayList<Comments>)
            }
        }


    }

    private fun showCommentDialog(respon: ResponsePost) {
        val dialog = BottomSheetDialog(requireContext())

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_comment)
        val bindingDialog = DialogCommentBinding.inflate(layoutInflater)
        dialog.setContentView(bindingDialog.root)

        dialogIsShowing = dialog.isShowing

        bindingDialog.btnSend.setOnClickListener {
            val comment = bindingDialog.etComment.text.toString()
            if(comment.isNotEmpty()){
                postVM.sendCommentExplore(respon.documentId, respon.username, comment)
                bindingDialog.etComment.setText("")
                postVM.getDataCommentExplor(respon.documentId)
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
    private fun setAppearancePost(respon: ResponsePost) {
        val storageReference = FirebaseStorage.getInstance().reference.child("post/${respon.image}").downloadUrl

        storageReference.addOnSuccessListener {
            try {
                Glide.with(requireActivity())
                    .load(it)
                    .into(binding.imgPost)
            }catch (e : java.lang.Exception){
                Log.e("ERROR", "err")
            }

        }

        binding.tvUsername.text = respon.username
        binding.tvContentPost.text = respon.content
        binding.tvAmounOfComment.text = "${respon.comments.size.toString()} komentar"
        binding.tvAccountName.text = respon.username

        userVM.retriveDataUser()

        userVM.dataProfile.observe(viewLifecycleOwner){
            if(it != null){
                val storageReference = FirebaseStorage.getInstance().reference.child("images/${it.profile.imageProfile}").downloadUrl

                storageReference.addOnSuccessListener {dataProfile->
                    try {
                        Glide.with(requireActivity())
                            .load(dataProfile)
                            .circleCrop()
                            .into(binding.imageProfile)
                    }catch (e : java.lang.Exception){
                        Log.e("ERROR", "err")
                    }

                }
            }
        }
    }

}