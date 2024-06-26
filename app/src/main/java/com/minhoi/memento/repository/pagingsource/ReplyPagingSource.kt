package com.minhoi.memento.repository.pagingsource

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.minhoi.memento.data.dto.question.ReplyContent
import com.minhoi.memento.data.network.ApiResult
import com.minhoi.memento.repository.question.QuestionRepository
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.first

class ReplyPagingSource(
    private val questionRepository: QuestionRepository,
    private val questionId: Long,
) :
    PagingSource<Int, ReplyContent>() {
    override fun getRefreshKey(state: PagingState<Int, ReplyContent>): Int? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(position)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ReplyContent> {
        val page = params.key ?: STARTING_KEY
        val loadData = questionRepository.getReplies(page, params.loadSize, questionId)
            .filterNot { it is ApiResult.Loading }
            .first()

        return when (loadData) {
            is ApiResult.Success -> {
                Log.d("ReplyPagingSource", "load: ${loadData.value.data}")
                LoadResult.Page(
                    data = loadData.value.data.data,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (page == loadData.value.data.pageInfo.totalPages || loadData.value.data.pageInfo.totalPages == 0) null else page + 1
                )
            }

            is ApiResult.Error -> {
                Log.d("ReplyPagingSource", "load: ${loadData.exception?.message}")
                LoadResult.Error(loadData.exception!!)
            }

            else -> {
                LoadResult.Error(Exception("오류"))
            }
        }
    }

    companion object {
        private const val STARTING_KEY = 1
    }
}