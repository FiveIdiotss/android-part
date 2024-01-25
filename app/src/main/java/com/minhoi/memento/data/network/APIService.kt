package com.minhoi.memento.data.network

import com.minhoi.memento.data.dto.MajorDto
import com.minhoi.memento.data.dto.SchoolDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface APIService {

    @GET("/api/schools")
    suspend fun getSchools(): Response<List<SchoolDto>>

    @GET("/api/school/{schoolName}")
    suspend fun getMajors(
        @Path(value = "schoolName") schoolName: String
    ): Response<List<MajorDto>>

    @POST("/api/member/signUp")
    suspend fun signUp(
        @Body member: MemberDto
    ): Response<String>

    @POST("api/member/signIn")
    suspend fun signIn(
        @Body loginRequest: LoginRequest
    ): Response<LoginResponse>

    @POST("login/email")
    suspend fun getVerificationCode(
        @Body emailVerificationRequest: EmailVerificationRequest
    ): Response<String>

    @POST("login/email/verify")
    suspend fun verificationWithCode(
        @Body verifyCodeRequest: VerifyCodeRequest
    ): Response<String>

    @POST("api/v1/clear")
    suspend fun clear(
        @Body clearDto: ClearDto
    ): Response<String>
}