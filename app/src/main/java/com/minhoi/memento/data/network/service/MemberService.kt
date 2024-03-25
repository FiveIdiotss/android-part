package com.minhoi.memento.data.network.service

import com.minhoi.memento.data.dto.MemberDTO
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface MemberService {
    @GET("api/member/{memberId}")
    suspend fun getMemberInfo(@Path("memberId") memberId: Long): Response<MemberDTO>

    @Multipart
    @POST("api/member/image")
    suspend fun uploadProfileImage(@Part image: MultipartBody.Part): Response<String>

    @POST("api/member/defaultImage")
    suspend fun setDefaultProfileImage(): Response<String>
}