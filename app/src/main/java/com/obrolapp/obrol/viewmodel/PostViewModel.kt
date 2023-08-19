package com.obrolapp.obrol.viewmodel

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.obrolapp.obrol.model.community.Members
import com.obrolapp.obrol.model.community.Message
import com.obrolapp.obrol.model.post.Comments
import com.obrolapp.obrol.model.post.Post
import com.obrolapp.obrol.model.post.ResponsePost
import java.text.SimpleDateFormat
import java.util.*

class PostViewModel : ViewModel() {

    private val firebaseFirestore = Firebase.firestore

    private var _dataPost : MutableLiveData<List<ResponsePost>> = MutableLiveData()
    val dataPost : LiveData<List<ResponsePost>> = _dataPost

    private var _responPost : MutableLiveData<String?> = MutableLiveData()
    val responPost : LiveData<String?> = _responPost

    private var _responExplor : MutableLiveData<List<ResponsePost>> = MutableLiveData()
    val responExplor : LiveData<List<ResponsePost>> = _responExplor

    private var _responComment : MutableLiveData<String?> = MutableLiveData()
    val responComment : LiveData<String?> = _responComment

    private var _responDataComments : MutableLiveData<List<Comments>> = MutableLiveData()
    val responDataComments : LiveData<List<Comments>> = _responDataComments

    private var _responCommentExplore : MutableLiveData<String?> = MutableLiveData()
    val responCommentExplore : LiveData<String?> = _responCommentExplore

    private var _responDataCommentsExplore : MutableLiveData<List<Comments>> = MutableLiveData()
    val responDataCommentsExplore : LiveData<List<Comments>> = _responDataCommentsExplore


    private fun postContent(post : Post, image : String){
        post.image = image

        if(post.codeCommunity != "global"){
            firebaseFirestore.collection("post").add(post).addOnCompleteListener {
                if(it.isSuccessful){
                    Log.i("INFO", "SUCCESS UPLOAD post")
                    _responPost.postValue("Berhasil")
                }else{
                    Log.i("INFO", "not success UPLOAD post")
                    _responPost.postValue(null)
                }
            }
                .addOnFailureListener {
                    Log.i("INFO", "failed UPLOAD post")
                    _responPost.postValue(null)
                }
        }else{
            firebaseFirestore.collection("exploration").add(post).addOnCompleteListener {
                if(it.isSuccessful){
                    Log.i("INFO", "SUCCESS UPLOAD post")
                    _responPost.postValue("Berhasil")
                }else{
                    Log.i("INFO", "not success UPLOAD post")
                    _responPost.postValue(null)
                }
            }
                .addOnFailureListener {
                    Log.i("INFO", "failed UPLOAD post")
                    _responPost.postValue(null)
                }
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun uploadImage(post : Post, imageUri : Uri){
        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val fileImageName = formatter.format(now) + "_${post.idUser}"
        val firebaseStorage = FirebaseStorage.getInstance().getReference("post/$fileImageName")

        firebaseStorage.putFile(imageUri).
        addOnSuccessListener {
            postContent(post, fileImageName)
            Log.i("INFO", "SUCCESS UPLOAD IMAGE")
        }.addOnFailureListener{
            Log.i("INFO", "FAILED UPLOAD IMAGE")
        }


    }


    fun getDataPost(community: MutableList<String>){

        firebaseFirestore.collection("post").get().addOnSuccessListener {
            if(!it.isEmpty){
                var post : MutableList<ResponsePost> = mutableListOf()
                for(i in it){

                    if(i["time"] != null && i["codeCommunity"] in community){
                        val documentId = i.id
                        val timestamp = i["time"] as com.google.firebase.Timestamp
                        val date = timestamp.toDate()
                        val data = ResponsePost(
                            documentId,
                            i["codeCommunity"].toString(),
                            i["idUser"].toString(),
                            i["username"].toString(),
                            i["content"].toString(),
                            date,
                            i["image"].toString(),
                            i["comments"] as List<Comments>
                        )

                        post.add(data)

                    }

                }
                _dataPost.postValue(post)
            }else{
                _dataPost.postValue(emptyList())
            }

        }.addOnFailureListener {

            _dataPost.postValue(emptyList())
        }
    }

    fun sendComment(codePost : String, username : String, comment : String){
        firebaseFirestore.collection("post").document(codePost).update("comments", FieldValue.arrayUnion(
            Comments(FirebaseAuth.getInstance().currentUser!!.uid, username, comment)
        ))
            .addOnCompleteListener {
                if(it.isSuccessful){
                    _responComment.postValue("Berhasil")
                }else{
                    _responComment.postValue(null)
                }

            }.addOnFailureListener {
                _responComment.postValue(null)
            }
    }

    fun getDataComment(documentId : String){
        FirebaseFirestore.getInstance().collection("post").document(documentId).get().addOnSuccessListener {
            if (it.exists()) {
                val dataComments = it.data?.get("comments") as? List<Map<*, *>>
                if (dataComments != null) {
                    val listComments: List<Comments> = dataComments.map { data ->
                        val time = data["time"] as com.google.firebase.Timestamp

                        Comments(
                            data["idUser"] as? String ?: "",
                            data["username"] as? String ?: "",
                            data["comment"] as? String ?: "",
                            time.toDate()
                        )
                    }
                    _responDataComments.postValue(listComments)
                    Log.i("DATA MESSAGE", listComments.toString())
                }

            } else {
                _responDataComments.postValue(emptyList())
            }
        }.addOnFailureListener {
            _responDataComments.postValue(emptyList())
        }
    }


    //exploration

    fun getDataExplor(){

        firebaseFirestore.collection("exploration").get().addOnSuccessListener {
            if(!it.isEmpty){
                var post : MutableList<ResponsePost> = mutableListOf()
                for(i in it){
                        val documentId = i.id
                        val timestamp = i["time"] as com.google.firebase.Timestamp
                        val date = timestamp.toDate()
                        val data = ResponsePost(
                            documentId,
                            i["codeCommunity"].toString(),
                            i["idUser"].toString(),
                            i["username"].toString(),
                            i["content"].toString(),
                            date,
                            i["image"].toString(),
                            i["comments"] as List<Comments>
                        )

                        post.add(data)

                }
                _responExplor.postValue(post)
            }else{
                _responExplor.postValue(emptyList())
            }

        }.addOnFailureListener {

            _responExplor.postValue(emptyList())
        }
    }

    fun sendCommentExplore(codePost : String, username : String, comment : String){
        firebaseFirestore.collection("exploration").document(codePost).update("comments", FieldValue.arrayUnion(
            Comments(FirebaseAuth.getInstance().currentUser!!.uid, username, comment)
        ))
            .addOnCompleteListener {
                if(it.isSuccessful){
                    _responCommentExplore.postValue("Berhasil")
                }else{
                    _responCommentExplore.postValue(null)
                }

            }.addOnFailureListener {
                _responCommentExplore.postValue(null)
            }
    }

    fun getDataCommentExplor(documentId : String){
        FirebaseFirestore.getInstance().collection("exploration").document(documentId).get().addOnSuccessListener {
            if (it.exists()) {
                val dataComments = it.data?.get("comments") as? List<Map<*, *>>
                if (dataComments != null) {
                    val listComments: List<Comments> = dataComments.map { data ->
                        val time = data["time"] as com.google.firebase.Timestamp

                        Comments(
                            data["idUser"] as? String ?: "",
                            data["username"] as? String ?: "",
                            data["comment"] as? String ?: "",
                            time.toDate()
                        )
                    }
                    _responDataCommentsExplore.postValue(listComments)
                    Log.i("DATA MESSAGE", listComments.toString())
                }

            } else {
                _responDataCommentsExplore.postValue(emptyList())
            }
        }.addOnFailureListener {
            _responDataCommentsExplore.postValue(emptyList())
        }
    }
}