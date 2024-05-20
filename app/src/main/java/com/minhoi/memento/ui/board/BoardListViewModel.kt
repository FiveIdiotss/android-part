package com.minhoi.memento.ui.board

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.minhoi.memento.pagingsource.BoardPagingSource
import com.minhoi.memento.repository.board.BoardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class BoardListViewModel @Inject constructor(
    private val boardRepository: BoardRepository,
) : ViewModel() {

    private val categoryQueryFlow = MutableStateFlow<String?>(null)
    private val schoolFilterFlow = MutableStateFlow<Boolean>(false)

    private val combineFilterFlow =
        combine(categoryQueryFlow, schoolFilterFlow) { category, school ->
            Pair(category, school)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getFilterBoardStream() =
        combineFilterFlow.flatMapLatest { (category, school) ->
            Pager(
                config = PagingConfig(pageSize = 20),
                pagingSourceFactory = {
                    BoardPagingSource(
                        boardRepository,
                        schoolFilter = school,
                        category = category
                    )
                }
            ).flow.cachedIn(viewModelScope)
        }

    fun setCategoryFilter(category: String?) {
        categoryQueryFlow.update { category }
    }

    fun setSchoolFilter(isChecked: Boolean) {
        schoolFilterFlow.update { isChecked }
    }

}