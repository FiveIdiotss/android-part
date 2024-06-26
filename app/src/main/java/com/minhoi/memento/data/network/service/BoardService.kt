package com.minhoi.memento.data.network.service

import com.minhoi.memento.base.CommonResponse
import com.minhoi.memento.data.dto.BoardContentResponse
import com.minhoi.memento.data.dto.BoardListResponse
import com.minhoi.memento.data.dto.MentorBoardPostDto
import com.minhoi.memento.data.dto.MentoringApplyRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface BoardService {
    @POST("api/board")
    suspend fun writeMenteeBoard(
        @Body mentorBoardPostDto: MentorBoardPostDto
    ): Response<CommonResponse<String>>

    @GET("api/boards/filter")
    suspend fun getBoardContents(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<CommonResponse<BoardListResponse>>

    @GET("api/boards/filter")
    suspend fun getFilterBoards(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("schoolFilter") schoolFilter: Boolean,
        @Query("favoriteFilter") favoriteFilter: Boolean,
        @Query("boardCategory") category: String?,
        @Query("keyWord") searchQuery: String?
    ): Response<CommonResponse<BoardListResponse>>

    @GET("api/board/{boardId}")
    suspend fun getBoardContent(@Path("boardId") boardId: Long): Response<CommonResponse<BoardContentResponse>>

    @POST("api/board/{boardId}")
    suspend fun applyMentoring(@Path("boardId") boardId: Long, @Body applyContent: MentoringApplyRequest): Response<CommonResponse<String>>

    @POST("api/board/favorite/{boardId}")
    suspend fun bookmarkBoard(@Path("boardId") boardId: Long): Response<CommonResponse<String>>

    @DELETE("api/board/favorite/{boardId}")
    suspend fun unBookmarkBoard(@Path("boardId") boardId: Long): Response<CommonResponse<String>>
}