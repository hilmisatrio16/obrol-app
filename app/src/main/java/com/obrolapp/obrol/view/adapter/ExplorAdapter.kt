package com.obrolapp.obrol.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.obrolapp.obrol.databinding.ItemExplorationsBinding
import com.obrolapp.obrol.model.post.ResponsePost

class ExplorAdapter(val data : ArrayList<ResponsePost>) : RecyclerView.Adapter<ExplorAdapter.ViewHolder>() {

    var onClickImage : ((ResponsePost)->Unit)? = null

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

    class ViewHolder(var binding : ItemExplorationsBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = ItemExplorationsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
                .into(holder.binding.imageContent)
        }

        holder.binding.imageContent.setOnClickListener {
            onClickImage?.invoke(dataPost)
        }


    }

    override fun getItemCount(): Int {
        //add
        return differ.currentList.size
    }

}