package com.minhoi.memento.utils

import android.widget.CalendarView
import androidx.databinding.BindingAdapter
import com.minhoi.memento.ui.board.BoardViewModel
import java.util.Calendar

object BindingAdapter {
    @BindingAdapter("onDateChanged")
    @JvmStatic
    fun setOnDateChangedListener(calendarView: CalendarView, viewModel: BoardViewModel?) {
        viewModel ?: return
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            viewModel.onDateSelected(year, month, dayOfMonth)
        }
    }
}