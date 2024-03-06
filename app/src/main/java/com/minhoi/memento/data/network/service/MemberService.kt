package com.minhoi.memento.data.network.service

import com.minhoi.memento.data.dto.MemberDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface MemberService {
    @GET("api/member/{memberId}")
    suspend fun getMemberInfo(@Path("memberId") memberId: Long): Response<MemberDTO>
}