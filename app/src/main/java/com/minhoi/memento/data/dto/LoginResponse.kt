package com.minhoi.memento.data.dto

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("memberDTO")
    val memberDTO: MemberDTO,

    @SerializedName("tokenDTO")
    val tokenDto: TokenDto,
)