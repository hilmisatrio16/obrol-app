package com.obrolapp.obrol.viewmodel

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.obrolapp.obrol.model.user.HistoryCommunity
import com.obrolapp.obrol.model.user.Profile
import com.obrolapp.obrol.model.user.User
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class UserViewModel : ViewModel() {

    private val firebaseAuth = Firebase.auth
    private val firebaseFire = Firebase.firestore

    private var _dataProfile : MutableLiveData<User?> = MutableLiveData()
    val dataProfile : LiveData<User?> = _dataProfile

    private var _responSignOut : MutableLiveData<String?> = MutableLiveData()
    val responSignOut : LiveData<String?> = _responSignOut

    private var _responSignIn : MutableLiveData<String?> = MutableLiveData()
    val responSignIn : LiveData<String?> = _responSignIn

    private var _responSignUp : MutableLiveData<String?> = MutableLiveData()
    val responSignUp : LiveData<String?> = _responSignUp

    private var _responDataCommunity : MutableLiveData<List<HistoryCommunity>?> = MutableLiveData()
    val responDataCommunity : LiveData<List<HistoryCommunity>?> = _responDataCommunity



    fun signUpAction(profile : Profile, imageUri: Uri){
        firebaseAuth.createUserWithEmailAndPassword(profile.email, profile.password).addOnCompleteListener {
            if(it.isSuccessful){
                Log.i("INFO", "SUCCESS")
                uploadImage(imageUri, profile)
            }else{
                _responSignUp.postValue(null)
                Log.i("INFO", "FAILED")
            }
        }.addOnFailureListener {
            _responSignUp.postValue(null)
        }
    }

    fun signInAction(email : String, password : String){
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful){
                Log.i("INFO", "SUCCESS ${firebaseAuth.currentUser?.uid}")
                _responSignIn.postValue("Berhasil")
            }
        }.addOnFailureListener {
            _responSignIn.postValue(null)
        }
    }

    fun signOutAction(){
        firebaseAuth.signOut()
        if(firebaseAuth.currentUser == null){
            _responSignOut.postValue("Berhasil")
        }else{
            _responSignOut.postValue(null)
        }
        Log.i("INFO", "${firebaseAuth.currentUser}")

    }

    private fun importDataToDatabase(profile: Profile, userId : String){

        firebaseFire.collection("users")
            .document(userId)
            .set(User(profile, listOf(HistoryCommunity("global", "global", "global"))))
            .addOnCompleteListener {
                if(it.isSuccessful){
                    Log.i("INFO", "SUCCESS INPUT DATA")
                    _responSignUp.postValue("Berhasil")
                }
            }
            .addOnFailureListener {
                Log.e("ERROR", "$it")
                _responSignUp.postValue(null)
            }
    }

    @SuppressLint("SimpleDateFormat")
    private fun uploadImage(imageUri : Uri, profile: Profile){
        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val fileImageName = formatter.format(now) + "_${profile.username}"
        val firebaseStorage = FirebaseStorage.getInstance().getReference("images/$fileImageName")

        firebaseStorage.putFile(imageUri).
                addOnSuccessListener {
                    importDataToDatabase(Profile(profile.fullName, profile.username, profile.email, profile.password, fileImageName), firebaseAuth.currentUser!!.uid)
                    Log.i("INFO", "SUCCESS UPLOAD IMAGE")
                    viewModelScope.launch {
                        Log.i("DATA HASIL", retriveDataUser().toString())
                    }
                }.addOnFailureListener{
            Log.i("INFO", "FAILED UPLOAD IMAGE")
        }


    }


    fun retriveDataUser(){

        firebaseFire.collection("users")
            .document(firebaseAuth.currentUser!!.uid)
            .get()
            .addOnSuccessListener {
                if(it.exists()){
                    val userData = it.data?.get("profile") as? Map<*, *>
                    val communityDataList = it.data?.get("community") as? List<Map<*, *>>
                    if(userData != null && communityDataList != null){
                        val profile = Profile(
                            userData["fullName"] as String,
                            userData["username"] as String,
                            userData["email"] as String,
                            userData["password"] as String,
                            userData["imageProfile"] as String
                        )

                        val communityList: List<HistoryCommunity> = communityDataList.map { communityData ->
                            HistoryCommunity(
                                communityData["nameCommunity"] as? String ?: "",
                                communityData["codeCommunity"] as? String ?: "",
                                communityData["imageCommunity"] as? String ?: ""
                            )
                        }
                        _dataProfile.postValue(User(profile, communityList))
                        Log.i("DATA USER", profile.toString() + communityList.toString())

                    }
                }
            }
            .addOnFailureListener {
                _dataProfile.postValue(null)
            }


    }

//    suspend fun retriveDataUser() : User?{
//        val deferred = CompletableDeferred<User?>()
//        firebaseFire.collection("users")
//            .document(firebaseAuth.currentUser!!.uid)
//            .get()
//            .addOnSuccessListener {
//                if(it.exists()){
//                    val userData = it.data?.get("profile") as? Map<*, *>
//                    if(userData != null){
//                        val user = User(
//                            Profile(
//                                userData["fullName"] as String,
//                                userData["username"] as String,
//                                userData["email"] as String,
//                                userData["password"] as String,
//                                userData["imageProfile"] as String
//                            ))
//
//                        deferred.complete(user)
//                    }
//                }
//            }
//            .addOnFailureListener {
//                deferred.complete(null)
//            }
//        return  deferred.await()
//
//    }




}