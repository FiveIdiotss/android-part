package com.minhoi.memento

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Html
import android.util.TypedValue
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

class HtmlImageGetter(private val textView: TextView) : Html.ImageGetter {
    override fun getDrawable(source: String): Drawable {
        val placeholder = DrawablePlaceHolder()
        Glide.with(textView.context)
            .load(source)
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?,
                ) {
                    val displayMetrics = textView.context.resources.displayMetrics
                    val width = displayMetrics.widthPixels
                    val height = 200.dpToPx(textView.context) // 세로 크기를 200dp로 고정
                    resource.setBounds(0, 0, width, height)
                    placeholder.setBounds(0, 0, width, height)
                    placeholder.drawable = resource
                    textView.text = textView.text
                }
                override fun onLoadCleared(placeholder: Drawable?) {}
            })
        return placeholder
    }

    fun Int.dpToPx(context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }

    class DrawablePlaceHolder : Drawable() {
        var drawable: Drawable? = null

        override fun draw(canvas: android.graphics.Canvas) {
            drawable?.draw(canvas)
        }

        override fun setAlpha(alpha: Int) {
            drawable?.alpha = alpha
        }

        override fun setColorFilter(colorFilter: android.graphics.ColorFilter?) {
            drawable?.colorFilter = colorFilter
        }

        override fun getOpacity(): Int {
            return drawable?.opacity ?: android.graphics.PixelFormat.TRANSPARENT
        }
    }
}