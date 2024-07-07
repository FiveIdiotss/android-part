package com.minhoi.memento.ui

import android.view.View
import androidx.core.view.isVisible
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.minhoi.memento.databinding.ShowEmptyContentLayoutBinding
import com.minhoi.memento.databinding.ShowRetryLayoutBinding

fun handleLoadState(
    adapter: RecyclerView.Adapter<*>,
    loadState: CombinedLoadStates,
    progressBar: View,
    recyclerView: View,
    retryLayout: ShowRetryLayoutBinding,
    emptyLayout: ShowEmptyContentLayoutBinding
) {
    // 로딩 중 일 때
    progressBar.isVisible = loadState.source.refresh is LoadState.Loading

    // 로딩 중이지 않을 때 (활성 로드 작업이 없고 에러가 없음)
    recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading

    // 로딩 에러 발생 시
    retryLayout.root.isVisible = loadState.source.refresh is LoadState.Error

    if (loadState.append.endOfPaginationReached) {
        // 아이템 수가 1보다 작으면 Empty View 보이도록 처리
        emptyLayout.root.isVisible = adapter.itemCount < 1
    }
}