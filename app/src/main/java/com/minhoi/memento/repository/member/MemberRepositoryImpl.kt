package com.minhoi.memento.repository.member

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
import javax.inject.Inject

class MemberRepositoryImpl @Inject constructor(
    private val memberService: MemberService,
    private val matchingService: MatchingService,
    private val notificationService: NotificationService,
) : MemberRepository {

    override suspend fun getMemberInfo(memberId: Long) = memberService.getMemberInfo(memberId)

    override suspend fun getApplyList(memberId: Long) = matchingService.getMyApply(memberId, "SEND")

    override fun getApplyInfo(applyId: Long) = safeFlow {
        memberService.getApplyInfo(applyId)
    }

    override fun getReceivedList(memberId: Long) = safeFlow {
        matchingService.getReceived(memberId, "RECEIVE")
    }

    override fun getMentorInfo(memberId: Long): Flow<ApiResult<List<MentoringMatchInfo>>> =
        safeFlow {
            matchingService.getMatchedMentoringInfo(memberId, BoardType.MENTEE)
        }

    override fun getMenteeInfo(memberId: Long): Flow<ApiResult<List<MentoringMatchInfo>>> =
        safeFlow {
            matchingService.getMatchedMentoringInfo(memberId, BoardType.MENTOR)
        }

    override suspend fun acceptApply(applyId: Long) = matchingService.acceptApply(applyId)

    override suspend fun rejectApply(applyId: Long) = matchingService.rejectApply(applyId)

    override fun uploadProfileImage(image: MultipartBody.Part) = safeFlow {
        memberService.uploadProfileImage(image)
    }

    override fun setDefaultProfileImage() = safeFlow {
        memberService.setDefaultProfileImage()
    }

    override fun getBookmarkBoards(): Flow<ApiResult<List<BoardContentDto>>> = flow {
        val response = memberService.getBookmarkBoards()
        if (response.isSuccessful) {
            emit(ApiResult.Success(response.body() ?: throw Exception("BookmarkBoards is null")))
        } else {
            emit(ApiResult.Error(message = response.message()))
        }
    }

    override fun getMemberBoards(memberId: Long) = safeFlow {
        memberService.getMemberBoards(memberId)
    }

    override fun saveFCMToken(token: String) = notificationService.saveToken(token)

}