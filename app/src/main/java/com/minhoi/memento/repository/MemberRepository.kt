package com.minhoi.memento.repository

import com.minhoi.memento.data.dto.MentoringMatchInfo
import com.minhoi.memento.data.dto.MentoringReceivedDto
import com.minhoi.memento.data.network.RetrofitClient
import com.minhoi.memento.data.model.BoardType
import com.minhoi.memento.data.network.ApiResult
import com.minhoi.memento.data.network.safeFlow
import com.minhoi.memento.data.network.service.MatchingService
import com.minhoi.memento.data.network.service.MemberService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MemberRepository {

    private val matchingService =
        RetrofitClient.getLoggedInInstance().create(MatchingService::class.java)

    private val memberService = RetrofitClient.getLoggedInInstance().create(MemberService::class.java)

    suspend fun getMemberInfo(memberId: Long) = memberService.getMemberInfo(memberId)

    suspend fun getApplyList(memberId: Long) = matchingService.getMyApply(memberId, "SEND")

    suspend fun getReceivedList(memberId: Long): Flow<List<MentoringReceivedDto>> = flow {
        val response = matchingService.getReceived(memberId, "RECEIVE")
        if (response.isSuccessful) {
            emit(response.body() ?: throw Exception("ReceivedList is null"))
        } else {
            throw Exception("Network error: ${response.code()}")
        }
    }

    suspend fun getMatchedMentoringInfo(memberId: Long): Flow<ApiResult<List<MentoringMatchInfo>>> = safeFlow {
        matchingService.getMatchedMentoringInfo(memberId, BoardType.MENTEE)
    }

    suspend fun acceptApply(applyId: Long) = matchingService.acceptApply(applyId)

    suspend fun rejectApply(applyId: Long) = matchingService.rejectApply(applyId)

}