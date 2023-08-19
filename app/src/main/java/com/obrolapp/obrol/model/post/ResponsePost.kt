package com.obrolapp.obrol.model.post

import java.util.*

data class ResponsePost(
    val documentId : String,
    val codeCommunity : String,
    val idUser : String,
    val username : String,
    val content : String,
    val time : Date = Date(),
    var image : String = "",
    var comments : List<Comments> = emptyList()
) : java.io.Serializable
