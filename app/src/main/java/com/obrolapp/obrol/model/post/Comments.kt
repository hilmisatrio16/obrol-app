package com.obrolapp.obrol.model.post

import java.util.Date

data class Comments (
    val idUser : String,
    val username : String,
    val comment : String,
    val time : Date = Date()
        )