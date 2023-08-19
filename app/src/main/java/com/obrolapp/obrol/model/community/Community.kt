package com.obrolapp.obrol.model.community

data class Community(
    val profile : ProfileCommunity,
    val members : List<Members>,
    val message : List<Message> = emptyList()
)
