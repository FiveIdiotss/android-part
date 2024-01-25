package com.minhoi.memento.data.dto

import com.google.gson.annotations.SerializedName

data class SchoolDto(
    @SerializedName("schoolId")
    val schoolId: Int,

    @SerializedName("name")
    val name: String
)
