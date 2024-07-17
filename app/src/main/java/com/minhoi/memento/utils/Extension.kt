package com.minhoi.memento.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point
import android.os.Build
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.NumberPicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.minhoi.memento.ui.common.dialog.ProgressDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import java.util.Locale

const val DEFAULT_INTERVAL = 5
const val MINUTES_MIN = 0
const val MINUTES_MAX = 60

@SuppressLint("PrivateApi", "DiscouragedApi")
fun TimePicker.setTimeInterval(
    timeInterval: Int = DEFAULT_INTERVAL,
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
    timeInterval: Int = DEFAULT_INTERVAL,
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
//fun <T> safeFlow(apiFunc: suspend () -> Response<CommonResponse<T>>): Flow<ApiResult<CommonResponse<T>>> =
//    flow {
//        val response = apiFunc()
//
//        if (response.isSuccessful) {
//            val body = response.body() ?: throw NullPointerException("서버와 통신에 오류가 발생하였습니다.")
//            emit(ApiResult.Success(body))
//        } else {
//            val body = response.errorBody() ?: throw NullPointerException("서버와 통신에 오류가 발생하였습니다.")
//            val s = Gson().fromJson(body.charStream(), CommonResponse::class.java)
//                ?: throw NullPointerException("서버와 통신에 오류가 발생하였습니다.")
//            emit(ApiResult.Error(Throwable(s.message)))
//        }
//    }.onStart {
//        emit(ApiResult.Loading)
//    }.retryWhen { cause, attempt ->
//        if (cause is IOException && attempt < 3) {
//            delay(3000L)
//            true
//        } else {
//            false
//        }
//    }.catch { e ->
//        when (e) {
//            is NullPointerException -> emit(ApiResult.Error(e, e.message))
//            is ConnectException -> emit(ApiResult.Error(e, "네트워크 오류가 발생했습니다. 인터넷 연결을 확인해주세요."))
//            else -> emit(ApiResult.Error(e, "네트워크 오류가 발생했습니다. 인터넷 연결을 확인해주세요."))
//        }
//    }.flowOn(Dispatchers.IO)

fun parseLocalDateTime(localDateTimeString: String): String {
    // 문자열을 LocalDateTime으로 파싱
    val dateTime = LocalDateTime.parse(localDateTimeString)

    // DateTimeFormatter를 사용하여 원하는 형식으로 포맷
    val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 a hh:mm:ss", Locale.KOREA)
    val formattedDateTime = dateTime.format(formatter)

    return formattedDateTime
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

// View 확장 함수로 키보드 숨기기 기능 구현
fun View.hideKeyboard() {
    val inputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    inputMethodManager?.hideSoftInputFromWindow(windowToken, 0)
}

fun String.toRelativeTime(): String {
    val dateTimeFormatter = DateTimeFormatterBuilder()
        .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
        .optionalStart()
        .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
        .optionalEnd()
        .toFormatter()

    val dateTime = LocalDateTime.parse(this, dateTimeFormatter)
    val now = LocalDateTime.now()
    val minutesDiff = ChronoUnit.MINUTES.between(dateTime, now)
    val hoursDiff = ChronoUnit.HOURS.between(dateTime, now)
    val daysDiff = ChronoUnit.DAYS.between(dateTime, now)

    return when {
        minutesDiff < 60 -> "${minutesDiff}분 전"
        hoursDiff < 24 -> "${hoursDiff}시간 전"
        else -> "${daysDiff}일 전"
    }
}

fun LifecycleOwner.repeatOnStarted(block: suspend CoroutineScope.() -> Unit) {
    lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED, block)
    }
}


