package com.obrolapp.obrol.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.obrolapp.obrol.databinding.ItemMembersBinding
import com.obrolapp.obrol.databinding.ItemPostBinding
import com.obrolapp.obrol.model.post.Post
import com.obrolapp.obrol.model.user.Profile

class MembersAdapter(val data : ArrayList<Profile>) : RecyclerView.Adapter<MembersAdapter.ViewHolder>() {

    //diffutil
    private var diffCallback = object  : DiffUtil.ItemCallback<Profile>(){
        override fun areItemsTheSame(oldItem: Profile, newItem: Profile): Boolean {
            return oldItem.username == newItem.username
        }

        override fun areContentsTheSame(oldItem: Profile, newItem: Profile): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }
    //add
    var differ = AsyncListDiffer(this, diffCallback)
    //add
    fun submitData(value: ArrayList<Profile>){
        differ.submitList(value)
    }

//

    class ViewHolder(var binding : ItemMembersBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = ItemMembersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //add
        var dataProfile = differ.currentList[position]

            val storageReference = FirebaseStorage.getInstance().reference.child("images/${dataProfile.imageProfile}").downloadUrl

            storageReference.addOnSuccessListener {
                Glide.with(holder.itemView)
                    .load(it)
                    .circleCrop()
                    .into(holder.binding.imageMember)
            }
            holder.binding.tvUsername.text = dataProfile.username

            if(position == 0){
                holder.binding.admin.visibility = View.VISIBLE
            }




    }

    override fun getItemCount(): Int {
        //add
        return differ.currentList.size
    }

}