package com.minhoi.memento.repository.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.minhoi.memento.data.dto.notification.NotificationListDto
import com.minhoi.memento.data.network.ApiResult
import com.minhoi.memento.repository.member.MemberRepository
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.first

class NotificationPagingSource(private val memberRepository: MemberRepository) : PagingSource<Int, NotificationListDto>() {

    override fun getRefreshKey(state: PagingState<Int, NotificationListDto>): Int? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(position)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NotificationListDto> {
        val page = params.key ?: STARTING_KEY
        val loadData = memberRepository.getNotificationList(
            page,
            params.loadSize
        ).filterNot { it is ApiResult.Loading }.first()

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