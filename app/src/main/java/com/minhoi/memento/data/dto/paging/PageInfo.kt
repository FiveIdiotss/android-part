package com.minhoi.memento.data.dto.paging

data class PageInfo(
    val page: Int,
    val size: Int,
    val totalElements: Int,
    val totalPages: Int
)
