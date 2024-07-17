package com.minhoi.memento.ui.board

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.minhoi.memento.repository.board.BoardRepository
import com.minhoi.memento.repository.pagingsource.BoardPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class BoardListViewModel @Inject constructor(
    private val boardRepository: BoardRepository,
) : ViewModel() {

    private val categoryQueryFlow = MutableStateFlow<String?>(null)
    private val schoolFilterFlow = MutableStateFlow<Boolean>(false)
    private val _searchQueryFlow = MutableStateFlow<String?>(null)
    val searchQueryFlow: StateFlow<String?> = _searchQueryFlow.asStateFlow()

    private val combineFilterFlow =
        combine(categoryQueryFlow, schoolFilterFlow) { category, school ->
            Pair(category, school)
        }

    private val combineSearchAndFilterFlow =
        combine(combineFilterFlow, searchQueryFlow.debounce(300)) { filter, search ->
            Pair(filter,search)
        }

    /** combine 한 Flow를 flatten 하는 이유는 combine은 최신 값을
     * 방출하지만, 최소 한 쌍이 방출되지 않으면 합쳐서 방출이 되지 않음 (ex -> 사용자가 boardCategory만 고른 경우).
     * 또한 둘 중 하나라도 변경되면 새로운 게시글을 가져와야 하기 때문 */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getFilterBoardBySearch() =
        combineSearchAndFilterFlow.flatMapLatest { (filter, search) ->
            Pager(
                config = PagingConfig(pageSize = 20),
                pagingSourceFactory = {
                    BoardPagingSource(
                        boardRepository,
                        schoolFilter = filter.second,
                        category = filter.first,
                        searchQuery = search
                    )
                }
            ).flow.cachedIn(viewModelScope)
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
                        category = category,
                        searchQuery = null
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

    fun setSearchQuery(query: String?) {
        _searchQueryFlow.update { query }
    }

}