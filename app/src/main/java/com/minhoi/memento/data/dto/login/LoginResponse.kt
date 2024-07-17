package com.minhoi.memento.data.dto.login

import com.google.gson.annotations.SerializedName
import com.minhoi.memento.data.dto.member.MemberDTO
import com.minhoi.memento.data.dto.member.TokenDto

data class LoginResponse(
    @SerializedName("memberDTO")
    val memberDTO: MemberDTO,

    @SerializedName("tokenDTO")
    val tokenDto: TokenDto,
)