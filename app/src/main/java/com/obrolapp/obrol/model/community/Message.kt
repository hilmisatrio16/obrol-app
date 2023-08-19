package com.obrolapp.obrol.model.community

import java.util.Date

data class Message(
    val idUser : String,
    val username : String,
    val message : String,
    val createAt : Date = Date()
)
