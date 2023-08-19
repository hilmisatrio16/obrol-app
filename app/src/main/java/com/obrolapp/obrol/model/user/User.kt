package com.obrolapp.obrol.model.user

data class User (
    val profile : Profile,
    val community : List<HistoryCommunity>
)