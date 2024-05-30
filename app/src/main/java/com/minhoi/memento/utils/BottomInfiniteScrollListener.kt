package com.minhoi.memento.utils

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class BottomInfiniteScrollListener(
    private val layoutManager: LinearLayoutManager,
    private val isLoading: () -> Boolean,
    private val isLastPage: () -> Boolean,
    private val loadMore: () -> Unit,
) : RecyclerView.OnScrollListener() {
    private val TAG = BottomInfiniteScrollListener::class.simpleName
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val lastPosition = layoutManager.findLastCompletelyVisibleItemPosition()
        Log.d(TAG, "onScrolled: $lastPosition $isLastPage $isLoading")
        if (isLastPage() || isLoading()) return

        //RecyclerView에서 "완전히 보이는" 마지막 항목의 개수가 총 item 개수보다 1개 적을 때 서버에 요청
        if (layoutManager.itemCount <= lastPosition + 1 && recyclerView.canScrollVertically(1).not()) {
            loadMore()
        }
    }
}