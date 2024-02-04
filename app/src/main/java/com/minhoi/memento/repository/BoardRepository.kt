package com.minhoi.memento.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.minhoi.memento.data.dto.BoardContentDto
import com.minhoi.memento.data.network.APIService
import com.minhoi.memento.data.network.RetrofitClient
import kotlinx.coroutines.flow.Flow

class BoardRepository {

    private val retrofitClient = RetrofitClient.getInstance().create(APIService::class.java)

    suspend fun getMenteeBoards() = retrofitClient.getMenteeBoards()

    fun getMenteeBoardsStream(pageSize: Int): Flow<PagingData<BoardContentDto>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = pageSize),
            pagingSourceFactory = { BoardPagingSource(retrofitClient) }
        ).flow
    }
}