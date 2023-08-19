package com.obrolapp.obrol.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.obrolapp.obrol.databinding.ItemCommentBinding
import com.obrolapp.obrol.databinding.ItemPostBinding
import com.obrolapp.obrol.model.post.Comments
import com.obrolapp.obrol.model.post.ResponsePost

class CommentsAdapter(val data : ArrayList<Comments>) : RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {

    var onClickComment : ((ResponsePost)->Unit)? = null
    //diffutil
    private var diffCallback = object  : DiffUtil.ItemCallback<Comments>(){
        override fun areItemsTheSame(oldItem: Comments, newItem: Comments): Boolean {
            return oldItem.time == newItem.time
        }

        override fun areContentsTheSame(oldItem: Comments, newItem: Comments): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }
    //add
    var differ = AsyncListDiffer(this, diffCallback)
    //add
    fun submitData(value: ArrayList<Comments>){
        differ.submitList(value)
    }

//

    class ViewHolder(var binding : ItemCommentBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //add
        var dataComment = differ.currentList[position]

        holder.binding.tvName.text = dataComment.username
        holder.binding.tvComment.text = dataComment.comment


    }

    override fun getItemCount(): Int {
        //add
        return differ.currentList.size
    }

}