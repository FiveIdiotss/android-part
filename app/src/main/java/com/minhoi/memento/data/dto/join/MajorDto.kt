package com.minhoi.memento.data.dto.join

import com.google.gson.annotations.SerializedName

data class MajorDto(
    @SerializedName("majorId")
    val majorId: Int,

    @SerializedName("name")
    val name: String
)
