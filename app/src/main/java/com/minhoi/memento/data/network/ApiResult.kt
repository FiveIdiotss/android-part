package com.minhoi.memento.data.network

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import retrofit2.Response

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

    /**
     * Retrofit API 호출 시, flow로 변환하고 성공, 실패, 빈 응답에 대한 처리를 위한 함수
     */
    fun <T> safeFlow(apiFunc: suspend () -> Response<T>): Flow<ApiResult<T>> = flow {
        try {
            val response = apiFunc.invoke()
            if (response.isSuccessful) {
                val body = response.body() ?: throw NullPointerException("Response body is null")
                emit(ApiResult.Success(body))
            }
        } catch (e: NullPointerException) {
            emit(ApiResult.Empty)
        } catch (e: HttpException) {
            emit(ApiResult.Error(e))
        } catch (e: Exception) {
            emit(ApiResult.Error(e, e.message))
        }
    }



