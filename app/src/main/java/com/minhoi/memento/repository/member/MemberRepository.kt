package com.minhoi.memento.repository.member

import com.minhoi.memento.data.dto.BoardContentDto
import com.minhoi.memento.data.dto.MemberDTO
import com.minhoi.memento.data.dto.MentoringApplyDto
import com.minhoi.memento.data.dto.MentoringApplyListDto
import com.minhoi.memento.data.dto.MentoringMatchInfo
import com.minhoi.memento.data.dto.MentoringReceivedDto
import com.minhoi.memento.data.network.ApiResult
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response

interface MemberRepository {

    suspend fun getMemberInfo(memberId: Long): Response<MemberDTO>

    suspend fun getApplyList(memberId: Long): Response<List<MentoringApplyListDto>>

    fun getApplyInfo(applyId: Long): Flow<ApiResult<MentoringApplyDto>>

    fun getReceivedList(memberId: Long): Flow<ApiResult<List<MentoringReceivedDto>>>

    fun getMentorInfo(memberId: Long): Flow<ApiResult<List<MentoringMatchInfo>>>

    fun getMenteeInfo(memberId: Long): Flow<ApiResult<List<MentoringMatchInfo>>>

    suspend fun acceptApply(applyId: Long): Response<String>

    suspend fun rejectApply(applyId: Long): Response<String>

    fun uploadProfileImage(image: MultipartBody.Part): Flow<ApiResult<String>>

    fun setDefaultProfileImage(): Flow<ApiResult<String>>

    fun getBookmarkBoards(): Flow<ApiResult<List<BoardContentDto>>>

    fun getMemberBoards(memberId: Long): Flow<ApiResult<List<BoardContentDto>>>

    fun saveFCMToken(token: String): Call<String>
}