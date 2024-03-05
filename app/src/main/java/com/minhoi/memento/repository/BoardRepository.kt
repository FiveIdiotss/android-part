package com.minhoi.memento.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.minhoi.memento.data.dto.BoardContentDto
import com.minhoi.memento.data.dto.MentoringApplyRequest
import com.minhoi.memento.data.network.APIService
import com.minhoi.memento.data.network.RetrofitClient
import com.minhoi.memento.data.model.BoardType
import com.minhoi.memento.data.network.ApiResult
import com.minhoi.memento.data.network.service.BoardService
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class BoardRepository {

    private val retrofitClient = RetrofitClient.getInstance().create(BoardService::class.java)
    private val loggedInRetrofitClient = RetrofitClient.getLoggedInInstance().create(BoardService::class.java)

    suspend fun getPreviewBoards() = retrofitClient.getAllMenteeBoards(BoardType.MENTEE, 1, 10)

    fun getMenteeBoardsStream(pageSize: Int): Flow<PagingData<BoardContentDto>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = pageSize),
            pagingSourceFactory = { BoardPagingSource(retrofitClient) }
        ).flow
    }

    suspend fun getBoardContent(boardId: Long) = retrofitClient.getBoardContent(boardId)

    suspend fun applyMentoring(boardId: Long, applyRequest: MentoringApplyRequest) = loggedInRetrofitClient.applyMentoring(boardId, applyRequest)

}