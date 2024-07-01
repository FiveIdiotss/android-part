package com.minhoi.memento.data.network.service

import com.minhoi.memento.base.CommonResponse
import com.minhoi.memento.data.dto.ApplyRejectRequest
import com.minhoi.memento.data.dto.MentoringApplyListDto
import com.minhoi.memento.data.dto.MentoringMatchInfo
import com.minhoi.memento.data.dto.MentoringReceivedDto
import com.minhoi.memento.data.model.BoardType
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MatchingService {

    @GET("api/myApply")
    suspend fun getMyApply(@Query("sendReceive") requestType: String): Response<CommonResponse<List<MentoringApplyListDto>>>

    @GET("api/myApply")
    suspend fun getReceived(@Query("sendReceive") requestType: String): Response<CommonResponse<List<MentoringReceivedDto>>>

    @GET("api/matching")
    suspend fun getMatchedMentoringInfo(@Query("boardType") boardType: BoardType): Response<CommonResponse<List<MentoringMatchInfo>>>

    @POST("api/apply/{applyId}")
    suspend fun acceptApply(@Path("applyId") applyId: Long): Response<CommonResponse<String>>

    @POST("api/reject/{applyId}")
    suspend fun rejectApply(@Body content: ApplyRejectRequest.RejectReason, @Path("applyId") applyId: Long): Response<CommonResponse<String>>

}