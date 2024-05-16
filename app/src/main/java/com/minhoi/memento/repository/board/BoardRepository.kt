package com.minhoi.memento.repository.board

import androidx.paging.PagingData
import com.minhoi.memento.data.dto.BoardContentDto
import com.minhoi.memento.data.dto.BoardContentResponse
import com.minhoi.memento.data.dto.BoardListResponse
import com.minhoi.memento.data.dto.MentoringApplyRequest
import com.minhoi.memento.data.network.ApiResult
import kotlinx.coroutines.flow.Flow

interface BoardRepository {

    fun getPreviewBoards(): Flow<ApiResult<BoardListResponse>>

    fun getMenteeBoardsStream(pageSize: Int): Flow<PagingData<BoardContentDto>>

    fun getBoardContent(boardId: Long): Flow<ApiResult<BoardContentResponse>>

    fun applyMentoring(boardId: Long, applyRequest: MentoringApplyRequest): Flow<ApiResult<String>>

    fun executeBookmark(boardId: Long): Flow<ApiResult<String>>

    fun executeUnBookmark(boardId: Long): Flow<ApiResult<String>>
}