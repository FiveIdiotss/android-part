package com.minhoi.memento.repository

import com.minhoi.memento.data.dto.VerifyCodeRequest
import com.minhoi.memento.data.dto.EmailVerificationRequest
import com.minhoi.memento.data.dto.CreateMemberRequest
import com.minhoi.memento.data.network.APIService
import com.minhoi.memento.data.network.RetrofitClient

class JoinRepository {

    private val retrofitClient = RetrofitClient.getInstance().create(APIService::class.java)

    suspend fun getSchools() = retrofitClient.getSchools()

    suspend fun getMajors(name: String) = retrofitClient.getMajors(name)

    suspend fun join(member: CreateMemberRequest) = retrofitClient.signUp(member)

    suspend fun verifyEmail(emailVerificationRequest: EmailVerificationRequest) = retrofitClient.getVerificationCode(emailVerificationRequest)

    suspend fun verifyCode(verifyCodeRequest: VerifyCodeRequest) = retrofitClient.verificationWithCode(verifyCodeRequest)
}