package com.minhoi.memento.data.network

import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retryWhen
import retrofit2.Response
import java.io.IOException

private const val RETRY_TIME_IN_MILLIS = 15_000L
private const val RETRY_ATTEMPT_COUNT = 3

sealed class ApiResult<out T> {
    data class Success<out T>(val value: T) : ApiResult<T>()
    data class Error(
        val exception: Throwable? = null,
        val message: String? = ""
    ) : ApiResult<Nothing>()
    object Empty : ApiResult<Nothing>()
    object Loading : ApiResult<Nothing>()

    /**
    safeFlow 함수에서 반환된 Flow를 collect하여 처리할 때 사용하는 함수
     */
    fun handleResponse(
        emptyMsg: String = "데이터가 없습니다.",
        onError: (Error) -> Unit,
        onSuccess: (T) -> Unit
    ) {
        when (this@ApiResult) {
            is Success -> onSuccess(value)
            is Error -> onError(this)
            is Empty -> onError(Error(message = emptyMsg))
            is Loading -> {}
        }
    }

//     Flow를 ApiResult로 변환하는 함수
//    fun <T> Flow<T>.toApiResult(): Flow<ApiResult<T>> {
//        return this.map<T, ApiResult<T>> {
//            Log.d("toApiResult", "Emitting Success: $it")
//            ApiResult.Success(it)
//        }.onStart {
//            Log.d("toApiResult", "Emitting Loading")
//            emit(ApiResult.Loading)
//        }.retryWhen { cause, attempt ->
//            if (cause is IOException && attempt < RETRY_ATTEMPT_COUNT) {
//                delay(RETRY_TIME_IN_MILLIS)
//                true
//            } else {
//                false
//            }
//        }.catch {
//            Log.d("toApiResult", "Emitting Error: $it")
//            emit(ApiResult.Error(it))
//        }
//    }
    fun <T> Flow<Response<T>>.toApiResult(): Flow<ApiResult<T>> {
        return this.map<Response<T>, ApiResult<T>> { response ->
            if (response.isSuccessful) {
                response.body()?.let {
                    ApiResult.Success(it)
                } ?: run {
                    ApiResult.Error(Exception("Empty Body"))
                }
            } else {
                ApiResult.Error(Throwable(response.message()))
            }
        }.onStart {
            Log.d("toApiResult", "Emitting Loading")
            emit(ApiResult.Loading)
        }.retryWhen { cause, attempt ->
            if (cause is IOException && attempt < RETRY_ATTEMPT_COUNT) {
                delay(RETRY_TIME_IN_MILLIS)
                true
            } else {
                false
            }
        }.catch { exception ->
            Log.d("toApiResult", "Emitting Error: $exception")
            emit(ApiResult.Error(exception))
        }
    }
}




