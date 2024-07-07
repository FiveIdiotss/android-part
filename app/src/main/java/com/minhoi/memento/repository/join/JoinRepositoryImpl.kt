package com.minhoi.memento.repository.join

import com.minhoi.memento.base.CommonResponse
import com.minhoi.memento.data.dto.CreateMemberRequest
import com.minhoi.memento.data.dto.EmailVerificationRequest
import com.minhoi.memento.data.dto.MajorDto
import com.minhoi.memento.data.dto.SchoolDto
import com.minhoi.memento.data.dto.VerifyCodeRequest
import com.minhoi.memento.data.model.safeFlow
import com.minhoi.memento.data.network.ApiResult
import com.minhoi.memento.data.network.service.JoinService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class JoinRepositoryImpl @Inject constructor(
    private val joinService: JoinService
) : JoinRepository {

    override fun getSchools(): Flow<ApiResult<CommonResponse<List<SchoolDto>>>> = safeFlow {
        joinService.getSchools()
    }

    override fun getMajors(name: String): Flow<ApiResult<CommonResponse<List<MajorDto>>>> =
        safeFlow {
            joinService.getMajors(name)
        }

    override fun join(member: CreateMemberRequest): Flow<ApiResult<CommonResponse<String>>> =
        safeFlow {
            joinService.signUp(member)
        }

    override fun verifyEmail(emailVerificationRequest: EmailVerificationRequest): Flow<ApiResult<String>> =
        flow {
            val response = joinService.getVerificationCode(emailVerificationRequest)
            if (response.isSuccessful && response.body()!!.contains("success")) {
                emit(ApiResult.Success(response.body() ?: ""))
            } else {
                emit(ApiResult.Error(Throwable(response.errorBody()?.string() ?: "Error")))
            }
        }.catch { e ->
            emit(ApiResult.Error(e))
        }

    override fun verifyCode(verifyCodeRequest: VerifyCodeRequest): Flow<ApiResult<String>> =
        flow {
            val response = joinService.verificationWithCode(verifyCodeRequest)
            if (response.isSuccessful && response.body()!!.contains("success")) {
                emit(ApiResult.Success(response.body() ?: ""))
            } else {
                emit(ApiResult.Error(Throwable(response.message() ?: "Error")))
            }
        }.catch { e ->
            emit(ApiResult.Error(e))
        }
}