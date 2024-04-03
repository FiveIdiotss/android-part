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
        } else {
            throw HttpException(response)
        }
    } catch (e: NullPointerException) {
        emit(ApiResult.Error(e, e.message))
    } catch (e: HttpException) {
        emit(ApiResult.Error(e, e.message))
    }
    catch (e: SocketTimeoutException) {
        emit(ApiResult.Error(e, "네트워크 오류가 발생했습니다. 다시 시도해 주세요."))
    }
    catch (e: Exception) {
        emit(ApiResult.Error(e, e.message))
    }
}

fun parseLocalDateTime(localDateTimeString: String): String {
    // 문자열을 LocalDateTime으로 파싱
    val dateTime = LocalDateTime.parse(localDateTimeString)

    // DateTimeFormatter를 사용하여 원하는 형식으로 포맷
    val formatter = DateTimeFormatter.ofPattern("hh:mm:ss", Locale.getDefault())
    val formattedDateTime = dateTime.format(formatter)

    // 오후/오전 텍스트 추가
    val amPm = if (dateTime.hour < 12) "오전" else "오후"
    return "$amPm $formattedDateTime"
}


private const val PROGRESS_DIALOG_TAG = "progress_dialog"
// FragmentManager 확장 함수로 ProgressDialog의 표시 여부를 확인
private val FragmentManager.isProgressDialogShowing: Boolean
    get() = findFragmentByTag(PROGRESS_DIALOG_TAG) != null

fun FragmentManager.showLoading() {
    if (!isProgressDialogShowing) {
        val progressDialog = ProgressDialog()
        progressDialog.show(this, PROGRESS_DIALOG_TAG)
    }
}

fun FragmentManager.hideLoading() {
    if (isProgressDialogShowing) {
        (findFragmentByTag(PROGRESS_DIALOG_TAG) as? ProgressDialog)?.dismiss()
    }
}

