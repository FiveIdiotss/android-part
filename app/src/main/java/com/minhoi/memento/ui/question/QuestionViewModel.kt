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
import com.minhoi.memento.pagingsource.QuestionPagingSource
import com.minhoi.memento.pagingsource.ReplyPagingSource
import com.minhoi.memento.repository.question.QuestionRepository
import com.minhoi.memento.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestionViewModel @Inject constructor(
    private val questionRepository: QuestionRepository,
) : ViewModel() {

    private val _questionContent = MutableStateFlow<UiState<QuestionContent>>(UiState.Loading)
    val questionContent: StateFlow<UiState<QuestionContent>> = _questionContent.asStateFlow()

    private val _replyState = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val replyState: StateFlow<UiState<Boolean>> = _replyState.asStateFlow()

    private val _postQuestionState = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val postQuestionState: StateFlow<UiState<Boolean>> = _postQuestionState.asStateFlow()

    fun getQuestions(pageSize: Int): Flow<PagingData<QuestionContent>> {
        return Pager(
            config = PagingConfig(pageSize = pageSize),
            pagingSourceFactory = { QuestionPagingSource(questionRepository) })
            .flow
            .cachedIn(viewModelScope)
    }

    fun getQuestion(questionId: Long) {
        viewModelScope.launch {
            questionRepository.getQuestion(questionId).collectLatest { result ->
                result.handleResponse(
                    onSuccess = { data ->
                        Log.d("QuestionVIewModel", "getQuestion: $data")
                        _questionContent.update { UiState.Success(data.questionContent) }
                    },
                    onError = { error ->
                        _questionContent.update { UiState.Error(error.exception) }
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

}