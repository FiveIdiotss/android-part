package com.minhoi.memento.repository.board

import com.minhoi.memento.base.CommonResponse
import com.minhoi.memento.data.dto.BoardContentResponse
import com.minhoi.memento.data.dto.BoardListResponse
import com.minhoi.memento.data.dto.MentoringApplyRequest
import com.minhoi.memento.data.network.ApiResult
import kotlinx.coroutines.flow.Flow

interface BoardRepository {

    fun getPreviewBoards(): Flow<ApiResult<CommonResponse<BoardListResponse>>>

    fun getBoardContent(boardId: Long): Flow<ApiResult<CommonResponse<BoardContentResponse>>>

    fun getFilterBoardContents(page: Int, size: Int, boardCategory: String?, schoolFilter: Boolean, searchQuery: String?): Flow<ApiResult<CommonResponse<BoardListResponse>>>

    fun getBoardContentsBySchool(page: Int, size: Int): Flow<ApiResult<CommonResponse<BoardListResponse>>>

    fun applyMentoring(boardId: Long, applyRequest: MentoringApplyRequest): Flow<ApiResult<CommonResponse<String>>>

    fun executeBookmark(boardId: Long): Flow<ApiResult<CommonResponse<String>>>

    fun executeUnBookmark(boardId: Long): Flow<ApiResult<CommonResponse<String>>>
}