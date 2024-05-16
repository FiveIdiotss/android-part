package com.minhoi.memento.repository.board

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.minhoi.memento.data.dto.BoardContentDto
import com.minhoi.memento.data.dto.MentoringApplyRequest
import com.minhoi.memento.data.network.RetrofitClient
import com.minhoi.memento.data.network.service.BoardService
import com.minhoi.memento.pagingsource.BoardPagingSource
import com.minhoi.memento.utils.safeFlow
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BoardRepositoryImpl @Inject constructor() : BoardRepository {

    private val retrofitClient = RetrofitClient.getInstance().create(BoardService::class.java)
    private val loggedInRetrofitClient = RetrofitClient.getLoggedInInstance().create(BoardService::class.java)

   override fun getPreviewBoards() = safeFlow {
       loggedInRetrofitClient.getAllMenteeBoards(1, 10)
   }

    override fun getMenteeBoardsStream(pageSize: Int): Flow<PagingData<BoardContentDto>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = pageSize),
            pagingSourceFactory = { BoardPagingSource(retrofitClient) }
        ).flow
    }

    override fun getBoardContent(boardId: Long) = safeFlow {
        retrofitClient.getBoardContent(boardId)
    }

    override fun applyMentoring(boardId: Long, applyRequest: MentoringApplyRequest) = safeFlow {
        loggedInRetrofitClient.applyMentoring(boardId, applyRequest)
    }

    override fun executeBookmark(boardId: Long) = safeFlow {
        loggedInRetrofitClient.bookmarkBoard(boardId)
    }

    override fun executeUnBookmark(boardId: Long) = safeFlow {
        loggedInRetrofitClient.unBookmarkBoard(boardId)
    }
}