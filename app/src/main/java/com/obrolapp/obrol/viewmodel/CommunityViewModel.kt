package com.obrolapp.obrol.viewmodel

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.obrolapp.obrol.model.community.Community
import com.obrolapp.obrol.model.community.Members
import com.obrolapp.obrol.model.community.Message
import com.obrolapp.obrol.model.community.ProfileCommunity
import com.obrolapp.obrol.model.user.HistoryCommunity
import com.obrolapp.obrol.model.user.Profile
import java.text.SimpleDateFormat
import java.util.*


class CommunityViewModel : ViewModel() {

    private val firebaseFirestore = Firebase.firestore
    private val firebaseAuth = Firebase.auth

    private var _responJoinCommunity : MutableLiveData<String?> = MutableLiveData()
    val responJoinCommunity : LiveData<String?> = _responJoinCommunity

    private var _responCreateCommunity : MutableLiveData<String?> = MutableLiveData()
    val responCreateCommunity : LiveData<String?> = _responCreateCommunity

    private var _responDataCommunity : MutableLiveData<ProfileCommunity?> = MutableLiveData()
    val responDataCommunity : LiveData<ProfileCommunity?> = _responDataCommunity

    private var _responMembersCommunity : MutableLiveData<List<Profile>?> = MutableLiveData()
    val responMembersCommunity : LiveData<List<Profile>?> = _responMembersCommunity

    private var _responSendMessage : MutableLiveData<String?> = MutableLiveData()
    val responSendMessage : LiveData<String?> = _responSendMessage

    private var _responDataMessage : MutableLiveData<List<Message>> = MutableLiveData()
    val responDataMessage : LiveData<List<Message>> = _responDataMessage


    private fun createCommunity(name : String, description : String, rules : String, type: String, image : String) {
        val collectionRef = firebaseFirestore.collection("community")
        val formatter = SimpleDateFormat("yyyyMMddhhmmss", Locale.getDefault())
        val now = Date()
        val code = createCodeCommunity() + formatter.format(now)
        val community = Community(ProfileCommunity(code, name,description,rules, type, image), listOf(
            Members(firebaseAuth.currentUser!!.uid, "Admin")
        ))
        collectionRef.document(code).set(community)
            .addOnSuccessListener {
                firebaseFirestore.collection("users").document(firebaseAuth.currentUser!!.uid)
                    .update("community", FieldValue.arrayUnion(HistoryCommunity(name,code,image))).addOnCompleteListener { data->
                        if(data.isSuccessful){
                            _responCreateCommunity.postValue("berhasil")
                            Log.i("INFO COMMUNITY", "BERHASIL")
                        }else{
                            _responCreateCommunity.postValue(null)
                        }
                    }

            }
            .addOnFailureListener {
                _responCreateCommunity.postValue(null)
                Log.i("INFO COMMUNITY", "GAGAL")
            }
    }
    private fun createCodeCommunity() : String {
        val characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        val codeLength = 2
        val random = Random()
        val sb = StringBuilder(codeLength)

        for (i in 0 until codeLength) {
            val randomIndex = random.nextInt(characters.length)
            sb.append(characters[randomIndex])
        }

        return sb.toString()
    }

    @SuppressLint("SimpleDateFormat")
    fun uploadImage(imageUri : Uri, name : String, description : String, rules : String, type: String){
        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val fileImageName = formatter.format(now) + "_${name}"
        val firebaseStorage = FirebaseStorage.getInstance().getReference("community/$fileImageName")

        firebaseStorage.putFile(imageUri).
        addOnSuccessListener {
            createCommunity(name, description, rules, type, fileImageName)
            Log.i("INFO", "SUCCESS UPLOAD IMAGE")
        }.addOnFailureListener{
            Log.i("INFO", "FAILED UPLOAD IMAGE")
        }


    }


    fun joinCommunity(code : String){
        Log.i("INFO JOIN", "COBA")
        firebaseFirestore.collection("community").document(code).get().addOnSuccessListener {
            if(it.exists()){
                Log.i("INFO JOIN", "BERHASIL ${it.data}")
                firebaseFirestore.collection("community").document(code).update("members", FieldValue.arrayUnion(Members(firebaseAuth.currentUser!!.uid, "Members")))
                    .addOnCompleteListener {dataInput->
                        if(dataInput.isSuccessful){
                            val dataCommunity = it.data?.get("profile") as? Map<*, *>

                            if (dataCommunity != null){
                                firebaseFirestore.collection("users").document(firebaseAuth.currentUser!!.uid)
                                    .update("community", FieldValue.arrayUnion(HistoryCommunity(dataCommunity["name"] as String,code,dataCommunity["image"] as String))).addOnCompleteListener { data->
                                        if(data.isSuccessful){
                                            Log.i("INFO JOIN", "BERHASIL ${it.data}")
                                            _responJoinCommunity.postValue("Berhasil")
                                        }else{
                                            _responJoinCommunity.postValue(null)
                                        }
                                    }
                            }
                        }
                    }

            }else{
                _responJoinCommunity.postValue(null)
                Log.i("INFO JOIN", "GAGAL CUY")
            }
        }
            .addOnFailureListener {
                _responJoinCommunity.postValue(null)
                Log.i("INFO JOIN", "GAGAL")
            }
    }

    fun sentMessage(username : String, message : String, codeCommunity : String){
        firebaseFirestore.collection("community").document(codeCommunity)
            .update("message", FieldValue.arrayUnion(Message(firebaseAuth.currentUser!!.uid,username,message))).addOnCompleteListener {
                if(it.isSuccessful){
                    _responSendMessage.postValue("Berhasil")
                }else{
                    _responSendMessage.postValue(null)
                }
            }
            .addOnFailureListener {
                _responSendMessage.postValue(null)
            }
    }

    fun getDataMessage(codeCommunity: String){
        FirebaseFirestore.getInstance().collection("community").document(codeCommunity).get().addOnSuccessListener {
            if(it.exists()){
                val dataMessage = it.data?.get("message") as? List<Map<*, *>>
                if(dataMessage != null){
                    val listMessage: List<Message> = dataMessage.map { data ->
                        val time = data["createAt"] as com.google.firebase.Timestamp

                        Message(
                            data["idUser"] as? String ?: "",
                            data["username"] as? String ?: "",
                            data["message"] as? String ?: "",
                            time.toDate()
                        )
                    }
                    _responDataMessage.postValue(listMessage)
                    Log.i("DATA MESSAGE", listMessage.toString())
                }

            }else{
                _responDataMessage.postValue(emptyList())
            }
        }
            .addOnFailureListener {
                _responDataMessage.postValue(emptyList())
            }
    }

    fun getDataCommunity(codeCommunity: String){
        FirebaseFirestore.getInstance().collection("community").document(codeCommunity).get().addOnSuccessListener {
            if(it.exists()){
                val dataCommunity = it.data?.get("profile") as? Map<*, *>
                if(dataCommunity != null){
                    val profileCommunity = ProfileCommunity(
                        dataCommunity["code"] as String,
                        dataCommunity["name"] as String,
                        dataCommunity["description"] as String,
                        dataCommunity["rules"] as String,
                        dataCommunity["type"] as String,
                        dataCommunity["image"] as String
                    )
                    _responDataCommunity.postValue(profileCommunity)
                }
            }else{
                _responDataCommunity.postValue(null)
            }
        }.addOnFailureListener {
            _responDataCommunity.postValue(null)
        }
    }

    fun getDataMembers(codeCommunity: String){
        FirebaseFirestore.getInstance().collection("community").document(codeCommunity).get().addOnSuccessListener {
            if(it.exists()){
                val dataMembers = it.data?.get("members") as? List<Map<*, *>>
                if(dataMembers != null){
                    val membersList: List<Members> = dataMembers.map { data ->
                        Members(
                            data["idUser"] as? String ?: "",
                            data["role"] as? String ?: ""
                        )
                    }
                    var listDataProfile : MutableList<Profile> = mutableListOf()
                    for (i in membersList){
                        FirebaseFirestore.getInstance().collection("users").document(i.idUser).get().addOnSuccessListener {dataProfile->
                            if(dataProfile.exists()){
                                val userData = dataProfile.data?.get("profile") as? Map<*, *>
                                if(userData != null){
                                    val profile = Profile(
                                        userData["fullName"] as String,
                                        userData["username"] as String,
                                        userData["email"] as String,
                                        userData["password"] as String,
                                        userData["imageProfile"] as String
                                    )
                                    listDataProfile.add(profile)

                                }
                            }
                            if(listDataProfile.size == membersList.size){
                                _responMembersCommunity.postValue(listDataProfile)
                                Log.i("INFO DATA USER", listDataProfile.toString())
                            }
                        }
                    }


                }
            }else{
                _responMembersCommunity.postValue(null)
            }
        }.addOnFailureListener {
            _responMembersCommunity.postValue(null)
        }
    }
}