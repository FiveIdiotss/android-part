package com.minhoi.memento.data.network

sealed class ApiResult<out T> {
    data class Success<out T>(val value: T) : ApiResult<T>()
    data class Error(
        val exception: Throwable? = null,
        val message: String? = ""
    ) : ApiResult<Nothing>()
    object Empty : ApiResult<Nothing>()

    /**
    safeFlow 함수에서 반환된 Flow를 collect하여 처리할 때 사용하는 함수
     */
    fun handleResponse(
        emptyMsg: String = "데이터가 없습니다.",
        errorMsg: String = "인터넷 연결을 확인해주세요.",
        onError: (String) -> Unit,
        onSuccess: (T) -> Unit
    ) {
        when (this@ApiResult) {
            is Success -> onSuccess(value)
            is Error -> onError(errorMsg)
            is Empty -> onError(emptyMsg)
        }
    }
}




