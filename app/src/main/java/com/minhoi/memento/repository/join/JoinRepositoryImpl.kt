package com.minhoi.memento.repository.join

import com.minhoi.memento.data.dto.VerifyCodeRequest
import com.minhoi.memento.data.dto.EmailVerificationRequest
import com.minhoi.memento.data.dto.CreateMemberRequest
import com.minhoi.memento.data.network.RetrofitClient
import com.minhoi.memento.data.network.service.JoinService
import javax.inject.Inject

class JoinRepositoryImpl @Inject constructor() : JoinRepository {

    private val retrofitClient = RetrofitClient.getInstance().create(JoinService::class.java)

    override suspend fun getSchools() = retrofitClient.getSchools()

    override suspend fun getMajors(name: String) = retrofitClient.getMajors(name)

    override suspend fun join(member: CreateMemberRequest) = retrofitClient.signUp(member)

    override suspend fun verifyEmail(emailVerificationRequest: EmailVerificationRequest) = retrofitClient.getVerificationCode(emailVerificationRequest)

    override suspend fun verifyCode(verifyCodeRequest: VerifyCodeRequest) = retrofitClient.verificationWithCode(verifyCodeRequest)
}