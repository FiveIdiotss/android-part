package com.minhoi.memento.ui.common

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ColumnItemDecoration(
    context: Context,
    marginInDp: Int
) : RecyclerView.ItemDecoration() {

    private val marginInPx: Int = (context.resources.displayMetrics.density * marginInDp).toInt()

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val itemCount = parent.adapter?.itemCount ?: 0

        if (itemCount == 0)
            return

        when (position) {
            0 -> {
                // First item
                outRect.left = 0
                outRect.right = marginInPx
            }
            itemCount - 1 -> {
                // Last item
                outRect.left = marginInPx
                outRect.right = 0
            }
            else -> {
                // Middle items
                outRect.left = marginInPx
                outRect.right = marginInPx
            }
        }
    }
}