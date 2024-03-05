package com.minhoi.memento.data.network.service

import com.minhoi.memento.data.dto.CreateMemberRequest
import com.minhoi.memento.data.dto.EmailVerificationRequest
import com.minhoi.memento.data.dto.MajorDto
import com.minhoi.memento.data.dto.SchoolDto
import com.minhoi.memento.data.dto.VerifyCodeRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface JoinService {
    @GET("/api/schools")
    suspend fun getSchools(): Response<List<SchoolDto>>

    @GET("/api/school/{schoolName}")
    suspend fun getMajors(
        @Path(value = "schoolName") schoolName: String
    ): Response<List<MajorDto>>

    @POST("/api/member/signUp")
    suspend fun signUp(
        @Body member: CreateMemberRequest
    ): Response<String>

    @POST("login/email")
    suspend fun getVerificationCode(
        @Body emailVerificationRequest: EmailVerificationRequest
    ): Response<String>

    @POST("login/email/verify")
    suspend fun verificationWithCode(
        @Body verifyCodeRequest: VerifyCodeRequest
    ): Response<String>

}