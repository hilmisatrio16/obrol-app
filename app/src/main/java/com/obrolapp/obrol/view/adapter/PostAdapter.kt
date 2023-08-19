package com.obrolapp.obrol.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.obrolapp.obrol.databinding.ItemPostBinding
import com.obrolapp.obrol.model.post.Post
import com.obrolapp.obrol.model.post.ResponsePost

class PostAdapter(val data : ArrayList<ResponsePost>) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    var onClickComment : ((ResponsePost)->Unit)? = null

    //diffutil
    private var diffCallback = object  : DiffUtil.ItemCallback<ResponsePost>(){
        override fun areItemsTheSame(oldItem: ResponsePost, newItem: ResponsePost): Boolean {
            return oldItem.content == newItem.content
        }

        override fun areContentsTheSame(oldItem: ResponsePost, newItem: ResponsePost): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }
    //add
    var differ = AsyncListDiffer(this, diffCallback)
    //add
    fun submitData(value: ArrayList<ResponsePost>){
        differ.submitList(value)
    }

//

    class ViewHolder(var binding : ItemPostBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //add
        var dataPost = differ.currentList[position]

        val storageReference = FirebaseStorage.getInstance().reference.child("post/${dataPost.image}").downloadUrl

        storageReference.addOnSuccessListener {
            Glide.with(holder.itemView)
                .load(it)
                .into(holder.binding.imgPost)
        }
        holder.binding.tvAccountName.text = dataPost.username
        holder.binding.tvContentPost.text = dataPost.content
        holder.binding.tvContentPost.setOnClickListener {
            if(holder.binding.tvContentPost.maxLines == 2){
                holder.binding.tvContentPost.maxLines = 10
            }else{
                holder.binding.tvContentPost.maxLines = 2
            }
        }

        holder.binding.tvAmounOfComment.text = "${dataPost.comments.size} komentar"
        holder.binding.btnComment.setOnClickListener {
            onClickComment?.invoke(dataPost)
        }
        holder.binding.btnCommentIcon.setOnClickListener {
            onClickComment?.invoke(dataPost)
        }

    }

    override fun getItemCount(): Int {
        //add
        return differ.currentList.size
    }

}