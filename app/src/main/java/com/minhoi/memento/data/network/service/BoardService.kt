package com.minhoi.memento.data.network.service

import com.minhoi.memento.base.CommonResponse
import com.minhoi.memento.data.dto.board.BoardContentResponse
import com.minhoi.memento.data.dto.board.BoardListResponse
import com.minhoi.memento.data.dto.mentoring.MentoringApplyRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface BoardService {
    @POST("api/board")
    @Multipart
    suspend fun postBoard(
        @Part ("request") mentorBoardPostDto: RequestBody,
        @Part images: List<MultipartBody.Part>? = null // 이미지 리스트가 없을 때는 null 처리
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

    @DELETE("api/board/{boardId}")
    suspend fun deleteBoardContent(@Path("boardId") boardId: Long): Response<CommonResponse<String>>

    @POST("api/board/{boardId}")
    suspend fun applyMentoring(@Path("boardId") boardId: Long, @Body applyContent: MentoringApplyRequest): Response<CommonResponse<String>>

    @POST("api/board/favorite/{boardId}")
    suspend fun bookmarkBoard(@Path("boardId") boardId: Long): Response<CommonResponse<String>>

    @DELETE("api/board/favorite/{boardId}")
    suspend fun unBookmarkBoard(@Path("boardId") boardId: Long): Response<CommonResponse<String>>
}