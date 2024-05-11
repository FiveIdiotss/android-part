package com.minhoi.memento.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.minhoi.memento.data.dto.BoardContentDto
import com.minhoi.memento.data.dto.MentoringApplyRequest
import com.minhoi.memento.data.network.RetrofitClient
import com.minhoi.memento.data.model.BoardType
import com.minhoi.memento.data.network.service.BoardService
import com.minhoi.memento.utils.safeFlow
import kotlinx.coroutines.flow.Flow

class BoardRepository {

    private val retrofitClient = RetrofitClient.getInstance().create(BoardService::class.java)
    private val loggedInRetrofitClient = RetrofitClient.getLoggedInInstance().create(BoardService::class.java)

    suspend fun getPreviewBoards() = retrofitClient.getAllMenteeBoards(1, 10)

    fun getMenteeBoardsStream(pageSize: Int): Flow<PagingData<BoardContentDto>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = pageSize),
            pagingSourceFactory = { BoardPagingSource(retrofitClient) }
        ).flow
    }

    suspend fun getBoardContent(boardId: Long) = safeFlow {
        retrofitClient.getBoardContent(boardId)
    }

    suspend fun applyMentoring(boardId: Long, applyRequest: MentoringApplyRequest) = safeFlow {
        loggedInRetrofitClient.applyMentoring(boardId, applyRequest)
    }

    suspend fun executeBookmark(boardId: Long) = safeFlow {
        loggedInRetrofitClient.bookmarkBoard(boardId)
    }

    suspend fun executeUnBookmark(boardId: Long) = safeFlow {
        loggedInRetrofitClient.unBookmarkBoard(boardId)
    }
}