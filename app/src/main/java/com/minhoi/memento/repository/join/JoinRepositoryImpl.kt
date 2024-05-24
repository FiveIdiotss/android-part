package com.minhoi.memento.repository.join

import com.minhoi.memento.data.dto.VerifyCodeRequest
import com.minhoi.memento.data.dto.EmailVerificationRequest
import com.minhoi.memento.data.dto.CreateMemberRequest
import com.minhoi.memento.data.network.service.JoinService
import javax.inject.Inject

class JoinRepositoryImpl @Inject constructor(
    private val joinService: JoinService
) : JoinRepository {

    override suspend fun getSchools() = joinService.getSchools()

    override suspend fun getMajors(name: String) = joinService.getMajors(name)

    override suspend fun join(member: CreateMemberRequest) = joinService.signUp(member)

    override suspend fun verifyEmail(emailVerificationRequest: EmailVerificationRequest) = joinService.getVerificationCode(emailVerificationRequest)

    override suspend fun verifyCode(verifyCodeRequest: VerifyCodeRequest) = joinService.verificationWithCode(verifyCodeRequest)
}