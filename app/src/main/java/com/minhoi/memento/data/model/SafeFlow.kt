package com.minhoi.memento.data.model

import com.google.gson.Gson
import com.minhoi.memento.base.CommonResponse
import com.minhoi.memento.data.network.ApiResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retryWhen
import retrofit2.Response
import java.io.IOException
import java.net.ConnectException

fun <T> safeFlow(apiFunc: suspend () -> Response<CommonResponse<T>>): Flow<ApiResult<CommonResponse<T>>> =
    flow {
        val response = apiFunc()

        if (response.isSuccessful) {
            val body = response.body() ?: throw NullPointerException(NETWORK_ERROR)
            emit(ApiResult.Success(body))
        } else {
            val body = response.errorBody() ?: throw NullPointerException(NETWORK_ERROR)
            val errorResponse = Gson().fromJson(body.charStream(), CommonResponse::class.java)
                ?: throw NullPointerException(NETWORK_ERROR)
            emit(ApiResult.Error(Throwable(errorResponse.message)))
        }
    }.onStart {
        emit(ApiResult.Loading)
    }.retryWhen { cause, attempt ->
        if (cause is IOException && attempt < 3) {
            delay(3000L)
            true
        } else {
            false
        }
    }.catch { e ->
        when (e) {
            is ConnectException -> emit(ApiResult.Error(e, "서버에 연결할 수 없습니다. 잠시 후 다시 시도해주세요."))
            is IOException -> emit(ApiResult.Error(e, NETWORK_ERROR))
            else -> emit(ApiResult.Error(e, e.message))
        }
    }.flowOn(Dispatchers.IO)

private const val NETWORK_ERROR = "서버와 통신에 오류가 발생했습니다."