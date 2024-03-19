package com.minhoi.memento.utils

import android.widget.CalendarView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.minhoi.memento.ui.board.BoardViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object BindingAdapter {
    @BindingAdapter("onDateChanged")
    @JvmStatic
    fun setOnDateChangedListener(calendarView: CalendarView, viewModel: BoardViewModel?) {
        viewModel ?: return
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            viewModel.onDateSelected(year, month, dayOfMonth)
        }
    }

    /**
     * 마지막 메세지 보낸 날짜가 오늘이면 시간만 표시하고 아니면 날짜만 표시하는 함수
     */
    @BindingAdapter("formatChatRoomDate")
    @JvmStatic
    fun formatChatRoomDate(view: TextView, date: String?) {
        date?.let {
            val today = LocalDate.now()

            if (LocalDateTime.parse(date).toLocalDate().isEqual(today)) {
                view.text = parseLocalDateTime(date.toString()).substring(0, 8)
            } else {
                view.text = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            }
        }
    }
}