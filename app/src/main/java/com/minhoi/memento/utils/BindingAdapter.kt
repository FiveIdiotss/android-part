package com.minhoi.memento.utils

import android.graphics.drawable.Drawable
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
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
                view.text = date.format(DateTimeFormatter.ofPattern("yyyy-mm-dd")).substring(0,10)
            }
        }
    }

    /**
     * 프로필 이미지를 불러오는 동안은 placeholder를 보여주고, 성공적으로 불러왔으면 imageView에 표시하는 함수
     */
    @BindingAdapter("app:imageUrl", "app:placeholder")
    @JvmStatic fun loadImage(imageView: ImageView, url: String?, placeholder: Drawable) {
        if (url != null) {
            Glide.with(imageView.context)
                .load(url)
                .placeholder(placeholder)
                .error(placeholder)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .apply(RequestOptions().fitCenter())
                .into(imageView)
        } else {
            imageView.setImageDrawable(placeholder)
        }
    }
}