package com.minhoi.memento.repository.member

import com.minhoi.memento.base.CommonResponse
import com.minhoi.memento.data.dto.chat.ApplyRejectRequest
import com.minhoi.memento.data.dto.board.BoardListResponse
import com.minhoi.memento.data.dto.mentoring.MentoringApplyListDto
import com.minhoi.memento.data.dto.member.TokenDto
import com.minhoi.memento.data.dto.notification.NotificationListResponse
import com.minhoi.memento.data.model.BoardType
import com.minhoi.memento.data.model.safeFlow
import com.minhoi.memento.data.network.ApiResult
import com.minhoi.memento.data.network.service.AuthService
import com.minhoi.memento.data.network.service.BoardService
import com.minhoi.memento.data.network.service.MatchingService
import com.minhoi.memento.data.network.service.MemberService
import com.minhoi.memento.data.network.service.NotificationService
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import javax.inject.Inject

class MemberRepositoryImpl @Inject constructor(
    private val memberService: MemberService,
    private val matchingService: MatchingService,
    private val notificationService: NotificationService,
    private val boardService: BoardService,
    private val authService: AuthService
) : MemberRepository {

    override fun checkLoginState(refreshToken: String): Flow<ApiResult<CommonResponse<TokenDto>>> = safeFlow {
        authService.getAccessToken("Bearer $refreshToken")
    }

    override suspend fun getMemberInfo(memberId: Long) = memberService.getMemberInfo(memberId)

    override suspend fun getApplyList(): Flow<ApiResult<CommonResponse<List<MentoringApplyListDto>>>> = safeFlow {
        matchingService.getMyApply("SEND")
    }

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

    override fun acceptApply(applyId: Long): Flow<ApiResult<CommonResponse<String>>> = safeFlow {
        matchingService.acceptApply(applyId)
    }

    override fun rejectApply(applyRejectRequest: ApplyRejectRequest): Flow<ApiResult<CommonResponse<String>>> = safeFlow {
        matchingService.rejectApply(applyRejectRequest.reason, applyRejectRequest.applyId)
    }

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

    override fun getUnreadNotificationCounts(): Flow<ApiResult<CommonResponse<Int>>> = safeFlow {
        memberService.getUnreadNotificationCounts()
    }

    override fun deleteNotification(notificationId: Long): Flow<ApiResult<CommonResponse<String>>> = safeFlow {
        memberService.deleteNotification(notificationId)
    }

    override fun saveFCMToken(token: String) = notificationService.saveToken(token)

    override fun signOut(): Flow<ApiResult<CommonResponse<String>>> = safeFlow {
        memberService.signOut()
    }

}