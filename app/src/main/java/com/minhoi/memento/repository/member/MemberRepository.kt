package com.minhoi.memento.repository.member

import com.minhoi.memento.base.CommonResponse
import com.minhoi.memento.data.dto.chat.ApplyRejectRequest
import com.minhoi.memento.data.dto.board.BoardListResponse
import com.minhoi.memento.data.dto.member.MemberDTO
import com.minhoi.memento.data.dto.mentoring.MentoringApplyDto
import com.minhoi.memento.data.dto.mentoring.MentoringApplyListDto
import com.minhoi.memento.data.dto.mentoring.MentoringMatchInfo
import com.minhoi.memento.data.dto.mentoring.MentoringReceivedDto
import com.minhoi.memento.data.dto.member.TokenDto
import com.minhoi.memento.data.dto.notification.NotificationListResponse
import com.minhoi.memento.data.network.ApiResult
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response

interface MemberRepository {

    fun checkLoginState(refreshToken: String): Flow<ApiResult<CommonResponse<TokenDto>>>

    suspend fun getMemberInfo(memberId: Long): Response<CommonResponse<MemberDTO>>

    suspend fun getApplyList(): Flow<ApiResult<CommonResponse<List<MentoringApplyListDto>>>>

    fun getApplyInfo(applyId: Long): Flow<ApiResult<CommonResponse<MentoringApplyDto>>>

    fun getReceivedList(): Flow<ApiResult<CommonResponse<List<MentoringReceivedDto>>>>

    fun getMentorInfo(): Flow<ApiResult<CommonResponse<List<MentoringMatchInfo>>>>

    fun getMenteeInfo(): Flow<ApiResult<CommonResponse<List<MentoringMatchInfo>>>>

    fun acceptApply(applyId: Long): Flow<ApiResult<CommonResponse<String>>>

    fun rejectApply(applyRejectRequest: ApplyRejectRequest): Flow<ApiResult<CommonResponse<String>>>

    fun uploadProfileImage(image: MultipartBody.Part): Flow<ApiResult<CommonResponse<String>>>

    fun setDefaultProfileImage(): Flow<ApiResult<CommonResponse<String>>>

    fun getMemberBoards(memberId: Long, page: Int, size: Int): Flow<ApiResult<CommonResponse<BoardListResponse>>>

    fun getBookmarkBoards(page: Int, size: Int): Flow<ApiResult<CommonResponse<BoardListResponse>>>

    fun getNotificationList(page: Int, size: Int): Flow<ApiResult<CommonResponse<NotificationListResponse>>>

    fun getUnreadNotificationCounts(): Flow<ApiResult<CommonResponse<Int>>>

    fun deleteNotification(notificationId: Long): Flow<ApiResult<CommonResponse<String>>>

    fun saveFCMToken(token: String): Call<String>

    fun signOut(): Flow<ApiResult<CommonResponse<String>>>
}