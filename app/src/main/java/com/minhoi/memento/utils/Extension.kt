package com.minhoi.memento.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point
import android.os.Build
import android.view.View
import android.view.WindowManager
import android.widget.NumberPicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.google.gson.Gson
import com.minhoi.memento.base.CommonResponse
import com.minhoi.memento.data.network.ApiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import retrofit2.Response
import java.net.ConnectException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

const val DEFAULT_INTERVAL = 5
const val MINUTES_MIN = 0
const val MINUTES_MAX = 60

@SuppressLint("PrivateApi", "DiscouragedApi")
fun TimePicker.setTimeInterval(
    timeInterval: Int = DEFAULT_INTERVAL
) {
    try {
        val fieldId: Int = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            // 안드로이드 10 이하 버전에서는 reflection을 사용
            val classForId = Class.forName("com.android.internal.R\$id")
            classForId.getField("minute").getInt(null)
        } else {
            // 안드로이드 11 이상 버전에서는 Resources.getSystem()을 사용
            context.resources.getIdentifier("minute", "id", "android")
        }
        (this.findViewById(fieldId) as NumberPicker).apply {
            minValue = MINUTES_MIN
            maxValue = MINUTES_MAX / timeInterval - 1
            displayedValues = getDisplayedValue(timeInterval)
            wrapSelectorWheel = false
            setOnValueChangedListener(null)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun getDisplayedValue(
    timeInterval: Int = DEFAULT_INTERVAL
): Array<String> {
    val minutesArray = ArrayList<String>()
    for (i in 0 until MINUTES_MAX step timeInterval) {
        minutesArray.add(String.format("%02d", i))
    }
    return minutesArray.toArray(arrayOf(""))
}

fun View.setOnSingleClickListener(onSingleClick: (View) -> Unit) {
    val singleClickListener = OnSingleClickListener { onSingleClick(it) }
    setOnClickListener(singleClickListener)
}

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun IntRange.overlaps(other: IntRange): Boolean {
    return this.first < other.last && this.last > other.first
}

// List<IntRange>를 받고, 새로운 IntRange가 주어진 리스트 내의 범위와 중복되는지 확인하는 함수
fun List<IntRange>.containsOverlap(newRange: IntRange): Boolean {
    // 리스트 내부의 각 IntRange와 새로운 IntRange가 중복되는지 확인.
    this.forEach {
        if (it.overlaps(newRange))
            return true
    }
    return false
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
fun <T> safeFlow(apiFunc: suspend () -> Response<CommonResponse<T>>): Flow<ApiResult<CommonResponse<T>>> =
    flow {
        val response = apiFunc()

        if (response.isSuccessful) {
            val body = response.body() ?: throw NullPointerException("서버와 통신에 오류가 발생하였습니다.")
            emit(ApiResult.Success(body))
        } else {
            val body = response.errorBody() ?: throw NullPointerException("서버와 통신에 오류가 발생하였습니다.")
            val s = Gson().fromJson(body.charStream(), CommonResponse::class.java)
            emit(ApiResult.Error(Throwable(s.message)))
        }
    }.onStart {
        emit(ApiResult.Loading)
    }.catch { e ->
        when (e) {
            is NullPointerException -> emit(ApiResult.Error(e, e.message))
            is ConnectException -> emit(ApiResult.Error(e, "네트워크 오류가 발생했습니다. 인터넷 연결을 확인해주세요."))
            else -> emit(ApiResult.Error(e, "네트워크 오류가 발생했습니다. 인터넷 연결을 확인해주세요."))
        }
    }

/*
    * List를 생산하는 Flow에서 ApiResult가 Success일 경우 List를, 그 이외인 경우 null을 반환하는 함수(UiState에 반영하지 않고 사용할 때)
 */
suspend fun <T> Flow<ApiResult<List<T>>>.extractSuccess(): List<T>? {
    return this.filter { it is ApiResult.Success }
        .map { (it as ApiResult.Success).value }
        .firstOrNull()
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

