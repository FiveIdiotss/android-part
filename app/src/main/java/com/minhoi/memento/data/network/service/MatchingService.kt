package com.minhoi.memento.data.network.service

import com.minhoi.memento.data.dto.MentoringApplyDto
import com.minhoi.memento.data.dto.MentoringApplyRequest
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

    @GET("api/myApply/{memberId}")
    suspend fun getMyApply(@Path("memberId") memberId: Long, @Query("sendReceive") requestType: String): Response<List<MentoringApplyDto>>

    @GET("api/myApply/{memberId}")
    suspend fun getReceived(@Path("memberId") memberId: Long, @Query("sendReceive") requestType: String): Response<List<MentoringReceivedDto>>

    @GET("api/matching/{memberId}")
    suspend fun getMatchedMentoringInfo(@Path("memberId") memberId: Long, @Query("boardType") boardType: BoardType): Response<List<MentoringMatchInfo>>

    @POST("api/apply/{applyId}")
    suspend fun acceptApply(@Path("applyId") applyId: Long): Response<String>

    @POST("api/deny/{applyId}")
    suspend fun rejectApply(@Path("applyId") applyId: Long): Response<String>

}