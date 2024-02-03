package com.minhoi.memento.utils

import android.view.View

class OnSingleClickListener(private var interval: Int = 600, private val onClickListener: (View) -> Unit)
    : View.OnClickListener {

    private var lastClickTime: Long = 0L

    override fun onClick(v: View?) {
        if (isSafe() && v != null) {
            lastClickTime = System.currentTimeMillis()
            onClickListener(v)
        }
    }

    private fun isSafe(): Boolean {
        return System.currentTimeMillis() - lastClickTime > interval
    }
}