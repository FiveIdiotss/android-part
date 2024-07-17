package com.minhoi.memento.repository.board

import com.minhoi.memento.base.CommonResponse
import com.minhoi.memento.data.dto.board.BoardContentResponse
import com.minhoi.memento.data.dto.board.BoardListResponse
import com.minhoi.memento.data.dto.mentoring.MentoringApplyRequest
import com.minhoi.memento.data.network.ApiResult
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface BoardRepository {

    fun getPreviewBoards(): Flow<ApiResult<CommonResponse<BoardListResponse>>>

    fun getBoardContent(boardId: Long): Flow<ApiResult<CommonResponse<BoardContentResponse>>>

    fun getFilterBoardContents(page: Int, size: Int, boardCategory: String?, schoolFilter: Boolean, searchQuery: String?): Flow<ApiResult<CommonResponse<BoardListResponse>>>

    fun getBoardContentsBySchool(page: Int, size: Int): Flow<ApiResult<CommonResponse<BoardListResponse>>>

    fun postBoard(boardContent: RequestBody, images: List<MultipartBody.Part>?): Flow<ApiResult<CommonResponse<String>>>

    fun applyMentoring(boardId: Long, applyRequest: MentoringApplyRequest): Flow<ApiResult<CommonResponse<String>>>

    fun deleteBoardContent(boardId: Long): Flow<ApiResult<CommonResponse<String>>>

    fun executeBookmark(boardId: Long): Flow<ApiResult<CommonResponse<String>>>

    fun executeUnBookmark(boardId: Long): Flow<ApiResult<CommonResponse<String>>>
}