package com.minhoi.memento.utils

import android.content.Context
import android.graphics.Point
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.DialogFragment

fun View.setOnSingleClickListener(onSingleClick: (View) -> Unit) {
    val singleClickListener = OnSingleClickListener { onSingleClick(it) }
    setOnClickListener(singleClickListener)
}

// DialogFragment 크기 조절
fun Context.dialogFragmentResize(dialogFragment: DialogFragment, width: Float, height: Float) {

    val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

    if (Build.VERSION.SDK_INT < 30) {

        val display = windowManager.defaultDisplay
        val size = Point()

        display.getSize(size)

        val window = dialogFragment.dialog?.window

        val x = (size.x * width).toInt()
        val y = (size.y * height).toInt()
        window?.setLayout(x, y)

    } else {

        val rect = windowManager.currentWindowMetrics.bounds

        val window = dialogFragment.dialog?.window

        val x = (rect.width() * width).toInt()
        val y = (rect.height() * height).toInt()

        window?.setLayout(x, y)
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

