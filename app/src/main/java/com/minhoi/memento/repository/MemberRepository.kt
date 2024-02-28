package com.minhoi.memento.repository

import com.minhoi.memento.data.dto.MentoringReceivedDto
import com.minhoi.memento.data.network.APIService
import com.minhoi.memento.data.network.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MemberRepository {

    private val loggedInRetrofitClient =
        RetrofitClient.getLoggedInInstance().create(APIService::class.java)

    suspend fun getApplyList(memberId: Long) = loggedInRetrofitClient.getMyApply(memberId, "SEND")

    suspend fun getReceivedList(memberId: Long): Flow<List<MentoringReceivedDto>> = flow {
        val response = loggedInRetrofitClient.getReceived(memberId, "RECEIVE")
        if (response.isSuccessful) {
            emit(response.body() ?: throw Exception("ReceivedList is null"))
        } else {
            throw Exception("Network error: ${response.code()}")
        }
    }

    suspend fun acceptApply(applyId: Long) = loggedInRetrofitClient.acceptApply(applyId)

    suspend fun rejectApply(applyId: Long) = loggedInRetrofitClient.rejectApply(applyId)
}