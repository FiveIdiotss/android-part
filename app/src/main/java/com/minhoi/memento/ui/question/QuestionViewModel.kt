package com.minhoi.memento.ui.question

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.minhoi.memento.data.dto.question.QuestionContent
import com.minhoi.memento.data.dto.question.QuestionPostRequest
import com.minhoi.memento.data.dto.question.ReplyContent
import com.minhoi.memento.repository.pagingsource.QuestionPagingSource
import com.minhoi.memento.repository.pagingsource.ReplyPagingSource
import com.minhoi.memento.repository.question.QuestionRepository
import com.minhoi.memento.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestionViewModel @Inject constructor(
    private val questionRepository: QuestionRepository,
) : ViewModel() {

    private val _questionContentState = MutableStateFlow<UiState<QuestionContent>>(UiState.Loading)
    val questionContentState: StateFlow<UiState<QuestionContent>> = _questionContentState.asStateFlow()

    private val _replyState = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val replyState: StateFlow<UiState<Boolean>> = _replyState.asStateFlow()

    private val _postQuestionState = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val postQuestionState: StateFlow<UiState<Boolean>> = _postQuestionState.asStateFlow()

    private val categoryQueryFlow = MutableStateFlow<String?>(null)
    private val schoolFilterFlow = MutableStateFlow<Boolean>(false)
    private val searchQueryFlow = MutableStateFlow<String?>(null)

    @OptIn(FlowPreview::class)
    private val questionFilterFlow = combine(
        schoolFilterFlow,
        categoryQueryFlow,
        searchQueryFlow.debounce(300L)
    ) { schoolFilter, category, searchKeyWord ->
        Triple(schoolFilter, category, searchKeyWord)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getQuestions(pageSize: Int) =
        questionFilterFlow.flatMapLatest { (schoolFilter, category, searchKeyWord) ->
            Pager(
                config = PagingConfig(pageSize = pageSize),
                pagingSourceFactory = {
                    QuestionPagingSource(
                        questionRepository,
                        schoolFilter,
                        category,
                        searchKeyWord
                    )
                })
                .flow
                .cachedIn(viewModelScope)
        }

    fun getQuestion(questionId: Long) {
        viewModelScope.launch {
            _questionContentState.update { UiState.Loading }
            questionRepository.getQuestion(questionId).collectLatest { result ->
                result.handleResponse(
                    onSuccess = { data ->
                        Log.d("QuestionVIewModel", "getQuestion: $data")
                        _questionContentState.update { UiState.Success(data.data.questionContent) }
                    },
                    onError = { error ->
                        _questionContentState.update { UiState.Error(error.exception) }
                    }
                )
            }
        }
    }

    fun getReplies(questionId: Long): Flow<PagingData<ReplyContent>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { ReplyPagingSource(questionRepository, questionId) })
            .flow
            .cachedIn(viewModelScope)
    }

    fun postQuestion(title: String, content: String) {
        _postQuestionState.update { UiState.Loading }
        viewModelScope.launch {
            questionRepository.postQuestion(QuestionPostRequest(title, content))
                .collectLatest { result ->
                    result.handleResponse(
                        onSuccess = {
                            _postQuestionState.update { UiState.Success(true) }
                        },
                        onError = { error ->
                            _postQuestionState.update { UiState.Error(error.exception) }
                        }
                    )
                }
        }
    }

    fun postReply(questionId: Long, body: String) {
        _replyState.update { UiState.Loading }
        viewModelScope.launch {
            questionRepository.postReply(questionId, body).collectLatest {
                it.handleResponse(
                    onSuccess = {
                        _replyState.update { UiState.Success(true) }
                    },
                    onError = { error ->
                        _replyState.update { UiState.Error(error.exception) }
                    }
                )
            }
        }
    }

    fun setCategoryFilter(category: String?) {
        categoryQueryFlow.update { category }
    }

    fun setSchoolFilter(isChecked: Boolean) {
        schoolFilterFlow.update { isChecked }
    }

    fun setSearchQuery(query: String?) {
        searchQueryFlow.update { query }
    }
}