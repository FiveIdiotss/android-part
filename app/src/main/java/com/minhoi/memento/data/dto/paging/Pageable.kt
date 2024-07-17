package com.minhoi.memento.data.dto.paging

import com.google.gson.annotations.SerializedName

data class Pageable(
    @SerializedName("offset") val offset: Int,
    @SerializedName("pageNumber") val pageNumber: Int,
    @SerializedName("pageSize") val pageSize: Int,
    @SerializedName("paged") val paged: Boolean,
    @SerializedName("unpaged") val unpaged: Boolean,
    @SerializedName("sort") val sort: Sort
)
