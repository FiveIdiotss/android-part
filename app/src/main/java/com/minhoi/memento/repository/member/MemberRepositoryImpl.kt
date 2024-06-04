package com.minhoi.memento.repository.member

import com.minhoi.memento.base.CommonResponse
import com.minhoi.memento.data.dto.BoardContentDto
import com.minhoi.memento.data.dto.BoardListResponse
import com.minhoi.memento.data.dto.MentoringMatchInfo
import com.minhoi.memento.data.dto.notification.NotificationListResponse
import com.minhoi.memento.data.network.RetrofitClient
import com.minhoi.memento.data.model.BoardType
import com.minhoi.memento.data.network.ApiResult
import com.minhoi.memento.data.network.service.BoardService
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
    private val boardService: BoardService
) : MemberRepository {

    override suspend fun getMemberInfo(memberId: Long) = memberService.getMemberInfo(memberId)

    override suspend fun getApplyList() = matchingService.getMyApply("SEND")

    override fun getApplyInfo(applyId: Long) = safeFlow {
        memberService.getApplyInfo(applyId)
    }

    override fun getReceivedList() = safeFlow {
        matchingService.getReceived("RECEIVE")
    }

    override fun getMentorInfo() = safeFlow {
            matchingService.getMatchedMentoringInfo(BoardType.MENTEE)
        }

    override fun getMenteeInfo() = safeFlow {
            matchingService.getMatchedMentoringInfo( BoardType.MENTOR)
        }

    override suspend fun acceptApply(applyId: Long) = matchingService.acceptApply(applyId)

    override suspend fun rejectApply(applyId: Long) = matchingService.rejectApply(applyId)

    override fun uploadProfileImage(image: MultipartBody.Part) = safeFlow {
        memberService.uploadProfileImage(image)
    }

    override fun setDefaultProfileImage() = safeFlow {
        memberService.setDefaultProfileImage()
    }

    override fun getMemberBoards(
        memberId: Long,
        page: Int,
        size: Int
    ): Flow<ApiResult<CommonResponse<BoardListResponse>>> = safeFlow {
        memberService.getMemberBoards(memberId, page, size)
    }

    override fun getBookmarkBoards(
        page: Int,
        size: Int,
    ): Flow<ApiResult<CommonResponse<BoardListResponse>>> = safeFlow {
        boardService.getFilterBoards(page, size, schoolFilter = false, favoriteFilter = true, null, null)
    }

    override fun getNotificationList(
        page: Int,
        size: Int
    ): Flow<ApiResult<CommonResponse<NotificationListResponse>>> = safeFlow {
        memberService.getNotificationList(page, size)
    }

    override fun saveFCMToken(token: String) = notificationService.saveToken(token)

}