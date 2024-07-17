package com.minhoi.memento.data.network.service

import com.minhoi.memento.base.CommonResponse
import com.minhoi.memento.data.dto.join.CreateMemberRequest
import com.minhoi.memento.data.dto.join.EmailVerificationRequest
import com.minhoi.memento.data.dto.join.MajorDto
import com.minhoi.memento.data.dto.join.SchoolDto
import com.minhoi.memento.data.dto.join.VerifyCodeRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface JoinService {
    @GET("/api/schools")
    suspend fun getSchools(): Response<CommonResponse<List<SchoolDto>>>

    @GET("/api/school/{schoolName}")
    suspend fun getMajors(
        @Path(value = "schoolName") schoolName: String
    ): Response<CommonResponse<List<MajorDto>>>

    @POST("/api/member/signUp")
    suspend fun signUp(
        @Body member: CreateMemberRequest
    ): Response<CommonResponse<String>>

    @POST("/api/email")
    suspend fun getVerificationCode(
        @Body emailVerificationRequest: EmailVerificationRequest
    ): Response<String>

    @POST("/api/email/verify")
    suspend fun verificationWithCode(
        @Body verifyCodeRequest: VerifyCodeRequest
    ): Response<String>

}