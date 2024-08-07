package com.minhoi.memento.repository.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.minhoi.memento.data.dto.question.QuestionContent
import com.minhoi.memento.data.network.ApiResult
import com.minhoi.memento.repository.question.QuestionRepository
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.first

class QuestionPagingSource(
    private val questionRepository: QuestionRepository,
    private val schoolFilter: Boolean = false,
    private val likeFilter: Boolean = false,
    private val boardCategory: String? = null,
    private val searchKeyWord: String? = null,
    private val memberFilter: Boolean = false,
    private val memberId: Long = -1
) : PagingSource<Int, QuestionContent>() {

    override fun getRefreshKey(state: PagingState<Int, QuestionContent>): Int? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(position)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, QuestionContent> {
        val page = params.key ?: STARTING_KEY
        val loadData =
            if (memberFilter) {
                questionRepository.getMyQuestions(page, params.loadSize, memberId)
                    .filterNot { it is ApiResult.Loading }.first()
            } else {
                questionRepository.getQuestions(
                    page,
                    params.loadSize,
                    schoolFilter,
                    likeFilter,
                    boardCategory,
                    searchKeyWord
                ).filterNot { it is ApiResult.Loading }.first()
            }

        return when (loadData) {
            is ApiResult.Success -> {
                LoadResult.Page(
                    data = loadData.value.data.content,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (page == loadData.value.data.pageInfo.totalPages || loadData.value.data.pageInfo.totalPages == 0) null else page + 1
                )
            }

            is ApiResult.Error -> {
                LoadResult.Error(loadData.exception!!)
            }

            else -> {
                LoadResult.Error(Exception("오류"))
            }
        }
    }

    companion object {
        const val STARTING_KEY = 1
    }
}