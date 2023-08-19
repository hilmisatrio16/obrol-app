package com.obrolapp.obrol.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.obrolapp.obrol.databinding.ItemGroupChatBinding
import com.obrolapp.obrol.model.user.HistoryCommunity

class ChatCommunityAdapter(val list : ArrayList<HistoryCommunity>) : RecyclerView.Adapter<ChatCommunityAdapter.ViewHolder>() {

    var onClickItemGroup : ((HistoryCommunity) -> Unit)? = null
    //diffutil
    private var diffCallback = object  : DiffUtil.ItemCallback<HistoryCommunity>(){
        override fun areItemsTheSame(oldItem: HistoryCommunity, newItem: HistoryCommunity): Boolean {
            return oldItem.codeCommunity == newItem.codeCommunity
        }

        override fun areContentsTheSame(oldItem: HistoryCommunity, newItem: HistoryCommunity): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }
    //add
    var differ = AsyncListDiffer(this, diffCallback)
    //add
    fun submitData(value: ArrayList<HistoryCommunity>){
        differ.submitList(value)
    }

//

    class ViewHolder(var binding : ItemGroupChatBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = ItemGroupChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //add
        var dataPost = differ.currentList[position]

        val storageReference = FirebaseStorage.getInstance().reference.child("community/${dataPost.imageCommunity}").downloadUrl

        storageReference.addOnSuccessListener {
            Glide.with(holder.itemView)
                .load(it)
                .circleCrop()
                .into(holder.binding.imageGroup)
        }
        holder.binding.community.text = dataPost.nameCommunity

        FirebaseFirestore.getInstance().collection("community").document(dataPost.codeCommunity).get()
            .addOnSuccessListener{
                if(it.exists()){
                    val data = it.data?.get("profile") as? Map<*, *>
                    if(data?.get("description") != null){
                        holder.binding.etDescription.text = data["description"].toString()
                        Log.i("data deskripsi", data!!["description"].toString())
                    }

                }
            }

        holder.binding.btnItemGroup.setOnClickListener {
            onClickItemGroup?.invoke(dataPost)
        }
    }

    override fun getItemCount(): Int {
        //add
        return differ.currentList.size
    }

}