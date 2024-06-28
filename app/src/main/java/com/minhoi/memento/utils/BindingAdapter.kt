package com.minhoi.memento.utils

import android.graphics.drawable.Drawable
import android.text.Html
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.minhoi.memento.HtmlImageGetter
import com.minhoi.memento.R
import com.minhoi.memento.ui.board.BoardViewModel
import java.time.LocalDate
import java.time.LocalDateTime

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
            val day = parseLocalDateTime(date.toString()).substring(0, 13)
            val time = parseLocalDateTime(date.toString()).substring(14, 22)

            if (LocalDateTime.parse(date).toLocalDate().isEqual(today)) {
                view.text = time
            } else {
                view.text = day
            }
        }
    }

    /**
     * 프로필 이미지를 불러오는 동안은 placeholder를 보여주고, 성공적으로 불러왔으면 imageView에 표시하는 함수
     */
    @BindingAdapter("imageUrl", "placeholder")
    @JvmStatic
    fun loadImage(imageView: ImageView, url: String?, placeholder: Drawable) {
        if (url == null) return

        Glide.with(imageView.context)
            .load(url)
            .placeholder(placeholder)
            .error(placeholder)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .apply(RequestOptions().fitCenter())
            .into(imageView)
    }

    @BindingAdapter("likeStatus")
    @JvmStatic
    fun setLikeIcon(imageView: ImageView, isLike: Boolean) {
        val drawableId = if (isLike) R.drawable.thumbicon else R.drawable.thumbicon_unliked
        imageView.setImageResource(drawableId)
    }

    @BindingAdapter("htmlText")
    @JvmStatic
    fun setHtmlText(view: TextView, html: String?) {
        html?.let {
            view.text = Html.fromHtml(it, Html.FROM_HTML_MODE_COMPACT, HtmlImageGetter(view), null)
        }
    }
}