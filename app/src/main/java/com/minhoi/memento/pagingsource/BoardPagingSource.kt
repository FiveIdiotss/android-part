package com.minhoi.memento.pagingsource

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.minhoi.memento.data.dto.BoardContentDto
import com.minhoi.memento.data.network.service.BoardService

class BoardPagingSource(private val api: BoardService) : PagingSource<Int, BoardContentDto>() {
    override fun getRefreshKey(state: PagingState<Int, BoardContentDto>): Int? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(position)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, BoardContentDto> {
        val page = params.key ?: STARTING_KEY
        return try {
            val response = api.getAllMenteeBoards(page, params.loadSize)
            val boardData = response.body()
            if (response.isSuccessful) {
                Log.d("ENDLESS", "load: ${response.body()?.content.toString()}")
                LoadResult.Page(
                    data = boardData?.content ?: emptyList(),
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (boardData?.pageInfo?.totalPages == page) null else page + 1
                )
            } else {
                Log.d("ENDLESS", "loadError: ${response.errorBody()} ")
                LoadResult.Page(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = null
                )
            }
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    companion object {
        private const val STARTING_KEY = 1
    }
}