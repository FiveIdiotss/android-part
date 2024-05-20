package com.minhoi.memento.repository.board

import com.minhoi.memento.data.dto.BoardContentResponse
import com.minhoi.memento.data.dto.BoardListResponse
import com.minhoi.memento.data.dto.MentoringApplyRequest
import com.minhoi.memento.data.network.ApiResult
import kotlinx.coroutines.flow.Flow

interface BoardRepository {

    fun getPreviewBoards(): Flow<ApiResult<BoardListResponse>>

    fun getBoardContents(page: Int, size: Int): Flow<ApiResult<BoardListResponse>>

    fun getBoardContent(boardId: Long): Flow<ApiResult<BoardContentResponse>>

    fun getBoardContentsByCategory(page: Int, size: Int, boardCategory: String): Flow<ApiResult<BoardListResponse>>

    fun getBoardContentsBySchool(page: Int, size: Int): Flow<ApiResult<BoardListResponse>>

    fun applyMentoring(boardId: Long, applyRequest: MentoringApplyRequest): Flow<ApiResult<String>>

    fun executeBookmark(boardId: Long): Flow<ApiResult<String>>

    fun executeUnBookmark(boardId: Long): Flow<ApiResult<String>>
}