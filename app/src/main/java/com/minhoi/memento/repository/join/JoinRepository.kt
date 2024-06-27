package com.minhoi.memento.repository.join

import com.minhoi.memento.base.CommonResponse
import com.minhoi.memento.data.dto.CreateMemberRequest
import com.minhoi.memento.data.dto.EmailVerificationRequest
import com.minhoi.memento.data.dto.MajorDto
import com.minhoi.memento.data.dto.SchoolDto
import com.minhoi.memento.data.dto.VerifyCodeRequest
import com.minhoi.memento.data.network.ApiResult
import kotlinx.coroutines.flow.Flow

interface JoinRepository {

    fun getSchools(): Flow<ApiResult<CommonResponse<List<SchoolDto>>>>

    fun getMajors(name: String): Flow<ApiResult<CommonResponse<List<MajorDto>>>>

    fun join(member: CreateMemberRequest): Flow<ApiResult<CommonResponse<String>>>

    fun verifyEmail(emailVerificationRequest: EmailVerificationRequest): Flow<ApiResult<String>>

    fun verifyCode(verifyCodeRequest: VerifyCodeRequest): Flow<ApiResult<String>>
}