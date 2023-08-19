package com.obrolapp.obrol.model.post

import java.util.Date

data class Post (
        val codeCommunity : String,
        val idUser : String,
        val username : String,
        val content : String,
        val time : Date = Date(),
        var image : String = "",
        var comments : List<Comments> = emptyList()
        )