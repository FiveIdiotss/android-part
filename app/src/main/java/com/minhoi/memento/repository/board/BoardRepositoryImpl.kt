package com.minhoi.memento.repository.board

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.minhoi.memento.data.dto.BoardContentDto
import com.minhoi.memento.data.dto.MentoringApplyRequest
import com.minhoi.memento.data.network.service.BoardService
import com.minhoi.memento.pagingsource.BoardPagingSource
import com.minhoi.memento.utils.safeFlow
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BoardRepositoryImpl @Inject constructor(
    private val boardService: BoardService,
) : BoardRepository {

    override fun getPreviewBoards() = safeFlow {
        boardService.getAllMenteeBoards(1, 10)
    }

    override fun getMenteeBoardsStream(pageSize: Int): Flow<PagingData<BoardContentDto>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = pageSize),
            pagingSourceFactory = { BoardPagingSource(boardService) }
        ).flow
    }

    override fun getBoardContent(boardId: Long) = safeFlow {
        boardService.getBoardContent(boardId)
    }

    override fun applyMentoring(boardId: Long, applyRequest: MentoringApplyRequest) = safeFlow {
        boardService.applyMentoring(boardId, applyRequest)
    }

    override fun executeBookmark(boardId: Long) = safeFlow {
        boardService.bookmarkBoard(boardId)
    }

    override fun executeUnBookmark(boardId: Long) = safeFlow {
        boardService.unBookmarkBoard(boardId)
    }
}