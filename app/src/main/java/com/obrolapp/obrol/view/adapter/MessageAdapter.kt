package com.obrolapp.obrol.view.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.obrolapp.obrol.databinding.ItemChatBinding
import com.obrolapp.obrol.databinding.ItemPostBinding
import com.obrolapp.obrol.model.community.Message
import com.obrolapp.obrol.model.post.Post
import java.text.SimpleDateFormat

class MessageAdapter(val data : ArrayList<Message>) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    private val firebaseAuth = Firebase.auth
    //diffutil
    private var diffCallback = object  : DiffUtil.ItemCallback<Message>(){
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.createAt == newItem.createAt
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }
    //add
    var differ = AsyncListDiffer(this, diffCallback)
    //add
    fun submitData(value: ArrayList<Message>){
        differ.submitList(value)
    }

//

    class ViewHolder(var binding : ItemChatBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //add
        val dataMessage = differ.currentList[position]

        if(firebaseAuth.currentUser?.uid != null){
            if(dataMessage.idUser == FirebaseAuth.getInstance().currentUser!!.uid){
                holder.binding.card.setCardBackgroundColor(Color.parseColor("#7492FB"))
                holder.binding.tvMessage.setTextColor(Color.parseColor("#FFFFFF"))
                holder.binding.tvUsername.text = "saya"
            }else{
                holder.binding.card.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
                holder.binding.tvMessage.setTextColor(Color.parseColor("#000000"))
                holder.binding.tvUsername.text = dataMessage.username
            }

            holder.binding.tvMessage.text = dataMessage.message
            holder.binding.tvTime.text = "${dataMessage.createAt.hours}.${dataMessage.createAt.minutes}"
        }




    }

    override fun getItemCount(): Int {
        //add
        return differ.currentList.size
    }

}