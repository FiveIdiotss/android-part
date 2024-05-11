package com.minhoi.memento.repository

import com.minhoi.memento.data.dto.BoardContentDto
import com.minhoi.memento.data.dto.MentoringMatchInfo
import com.minhoi.memento.data.network.RetrofitClient
import com.minhoi.memento.data.model.BoardType
import com.minhoi.memento.data.network.ApiResult
import com.minhoi.memento.data.network.service.MatchingService
import com.minhoi.memento.data.network.service.MemberService
import com.minhoi.memento.data.network.service.NotificationService
import com.minhoi.memento.utils.safeFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody

class MemberRepository {

    private val matchingService =
        RetrofitClient.getLoggedInInstance().create(MatchingService::class.java)

    private val memberService = RetrofitClient.getLoggedInInstance().create(MemberService::class.java)
    private val notificationService = RetrofitClient.getLoggedInInstance().create(NotificationService::class.java)

    suspend fun getMemberInfo(memberId: Long) = memberService.getMemberInfo(memberId)

    suspend fun getApplyList(memberId: Long) = matchingService.getMyApply(memberId, "SEND")

    suspend fun getApplyInfo(applyId: Long) = safeFlow {
        memberService.getApplyInfo(applyId)
    }
    suspend fun getReceivedList(memberId: Long) = safeFlow {
        matchingService.getReceived(memberId, "RECEIVE")
    }

    suspend fun getMentorInfo(memberId: Long): Flow<ApiResult<List<MentoringMatchInfo>>> = safeFlow {
        matchingService.getMatchedMentoringInfo(memberId, BoardType.MENTEE)
    }

    suspend fun getMenteeInfo(memberId: Long): Flow<ApiResult<List<MentoringMatchInfo>>> = safeFlow {
        matchingService.getMatchedMentoringInfo(memberId, BoardType.MENTOR)
    }

    suspend fun acceptApply(applyId: Long) = matchingService.acceptApply(applyId)

    suspend fun rejectApply(applyId: Long) = matchingService.rejectApply(applyId)

    suspend fun uploadProfileImage(image: MultipartBody.Part) = safeFlow {
        memberService.uploadProfileImage(image)
    }

    suspend fun setDefaultProfileImage() = safeFlow {
        memberService.setDefaultProfileImage()
    }

    suspend fun getBookmarkBoards(): Flow<ApiResult<List<BoardContentDto>>> = flow {
        val response = memberService.getBookmarkBoards()
        if (response.isSuccessful) {
            emit(ApiResult.Success(response.body() ?: throw Exception("BookmarkBoards is null")))
        } else {
            emit(ApiResult.Error(message = response.message()))
        }
    }

    suspend fun getMemberBoards(memberId: Long) = safeFlow {
        memberService.getMemberBoards(memberId)
    }

    fun saveFCMToken(token: String) = notificationService.saveToken(token)

}