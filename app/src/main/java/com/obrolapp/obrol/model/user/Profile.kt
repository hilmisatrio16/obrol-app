package com.obrolapp.obrol.model.user

data class Profile(
    val fullName : String,
    val username : String,
    val email : String,
    val password : String,
    var imageProfile : String = ""
)
