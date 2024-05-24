package com.minhoi.memento.repository.join

import com.minhoi.memento.data.dto.CreateMemberRequest
import com.minhoi.memento.data.dto.EmailVerificationRequest
import com.minhoi.memento.data.dto.MajorDto
import com.minhoi.memento.data.dto.SchoolDto
import com.minhoi.memento.data.dto.VerifyCodeRequest
import retrofit2.Response

interface JoinRepository {

    suspend fun getSchools(): Response<List<SchoolDto>>

    suspend fun getMajors(name: String): Response<List<MajorDto>>

    suspend fun join(member: CreateMemberRequest): Response<String>

    suspend fun verifyEmail(emailVerificationRequest: EmailVerificationRequest): Response<String>

    suspend fun verifyCode(verifyCodeRequest: VerifyCodeRequest): Response<String>
}