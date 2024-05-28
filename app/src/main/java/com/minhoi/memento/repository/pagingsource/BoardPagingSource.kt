package com.minhoi.memento.repository.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.minhoi.memento.data.dto.BoardContentDto
import com.minhoi.memento.data.network.ApiResult
import com.minhoi.memento.data.network.ApiResult.Empty.toApiResult
import com.minhoi.memento.repository.board.BoardRepository
import kotlinx.coroutines.flow.first

class BoardPagingSource(
    private val boardRepository: BoardRepository,
    private val schoolFilter: Boolean = false,
    private val category: String?,
    private val searchQuery: String?

) : PagingSource<Int, BoardContentDto>() {
    override fun getRefreshKey(state: PagingState<Int, BoardContentDto>): Int? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(position)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, BoardContentDto> {
        val page = params.key ?: STARTING_KEY
        val loadData = boardRepository.getFilterBoardContents(page, params.loadSize, category, schoolFilter, searchQuery).first()

        return when (loadData) {

            is ApiResult.Success -> {
                LoadResult.Page(
                    data = loadData.value.data.content,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (page == loadData.value.data.pageInfo.totalPages || loadData.value.data.pageInfo.totalPages == 0) null else page + 1
                )
            }

            is ApiResult.Error -> LoadResult.Error(loadData.exception!!)
            else -> LoadResult.Error(Exception("오류"))
        }
    }

    companion object {
        private const val STARTING_KEY = 1
    }
}