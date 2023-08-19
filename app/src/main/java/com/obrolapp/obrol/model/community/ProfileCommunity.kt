package com.obrolapp.obrol.model.community

data class ProfileCommunity(
    val code: String,
    val name: String,
    val description : String,
    val rules : String,
    val type : String,
    val image : String = ""
)
