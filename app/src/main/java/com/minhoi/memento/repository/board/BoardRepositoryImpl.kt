package com.minhoi.memento.repository.board

import com.minhoi.memento.base.CommonResponse
import com.minhoi.memento.data.dto.BoardListResponse
import com.minhoi.memento.data.dto.MentoringApplyRequest
import com.minhoi.memento.data.network.ApiResult
import com.minhoi.memento.data.network.service.BoardService
import com.minhoi.memento.utils.safeFlow
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BoardRepositoryImpl @Inject constructor(
    private val boardService: BoardService,
) : BoardRepository {

    override fun getPreviewBoards() = safeFlow {
        boardService.getBoardContents(1, 10)
    }

    override fun getFilterBoardContents(
        page: Int,
        size: Int,
        boardCategory: String?,
        schoolFilter: Boolean,
        searchQuery: String?
    ): Flow<ApiResult<CommonResponse<BoardListResponse>>> = safeFlow {
        boardService.getFilterBoards(page, size, schoolFilter, false, boardCategory, searchQuery)
    }

    override fun getBoardContentsBySchool(page: Int, size: Int): Flow<ApiResult<CommonResponse<BoardListResponse>>> = safeFlow {
        boardService.getFilterBoards(page, size,
            schoolFilter = true,
            favoriteFilter = false,
            category = null,
            searchQuery = null
        )
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