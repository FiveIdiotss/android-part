package com.minhoi.memento.ui.question

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.Gson
import com.minhoi.memento.data.dto.question.QuestionContent
import com.minhoi.memento.data.dto.question.QuestionPostRequest
import com.minhoi.memento.data.dto.question.ReplyContent
import com.minhoi.memento.repository.pagingsource.QuestionPagingSource
import com.minhoi.memento.repository.pagingsource.ReplyPagingSource
import com.minhoi.memento.repository.question.QuestionRepository
import com.minhoi.memento.ui.UiState
import com.minhoi.memento.utils.FileManager
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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@HiltViewModel
class QuestionViewModel @Inject constructor(
    private val fileManager: FileManager,
    private val questionRepository: QuestionRepository,
) : ViewModel() {

    private val _questionContentState = MutableStateFlow<UiState<QuestionContent>>(UiState.Loading)
    val questionContentState: StateFlow<UiState<QuestionContent>> = _questionContentState.asStateFlow()

    private val _replyState = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val replyState: StateFlow<UiState<Boolean>> = _replyState.asStateFlow()

    private val _postQuestionState = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val postQuestionState: StateFlow<UiState<Boolean>> = _postQuestionState.asStateFlow()

    private val postCategory = MutableStateFlow<String>("")

    private val _postImages = MutableStateFlow<List<Uri>>(emptyList())
    val postImages: StateFlow<List<Uri>> = _postImages.asStateFlow()

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
    fun getQuestions() =
        questionFilterFlow.flatMapLatest { (schoolFilter, category, searchKeyWord) ->
            Pager(
                config = PagingConfig(pageSize = 20),
                pagingSourceFactory = {
                    QuestionPagingSource(
                        questionRepository,
                        schoolFilter = schoolFilter,
                        boardCategory = category,
                        searchKeyWord = searchKeyWord
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

    fun selectQuestionCategory(category: String) {
        postCategory.update { category }
    }

    fun addPostImages(image: List<Uri>) {
        _postImages.update { _postImages.value + image }
    }

    fun removePostImageAt(position: Int) {
        _postImages.update { postImages ->
            postImages.toMutableList().apply { removeAt(position) }
        }
    }

    fun postQuestion(title: String, content: String) {
        _postQuestionState.update { UiState.Loading }
        viewModelScope.launch {
            val question = QuestionPostRequest(title, content, postCategory.value, "QUEST")
            val questionJson = Gson().toJson(question)
            val questionRequestBody =
                questionJson.toRequestBody("application/json".toMediaTypeOrNull())
            val images = _postImages.value.map {
                val fileType = fileManager.getFileMimeType(it)
                fileManager.uriToMultipartBodyPart(it, fileType!!, "images")!!
            }
            questionRepository.postQuestion(questionRequestBody, images)
                .collectLatest { result ->
                    result.handleResponse(
                        onSuccess = {
                            _postQuestionState.update { UiState.Success(true) }
                        },
                        onError = { error ->
                            Log.d("QuestionViewModel", "postQuestion: ${error.exception?.message}")
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

    fun executeLike(questionId: Long, isLike: Boolean) {
        viewModelScope.launch {
            when (isLike) {
                true -> questionRepository.unExecuteLike(questionId)
                false -> questionRepository.executeLike(questionId)
            }.collectLatest {
                it.handleResponse(
                    onSuccess = {
                        getQuestion(questionId)
                    },
                    onError = { error ->
                        _questionContentState.update { UiState.Error(error.exception) }
                    }
                )
            }
        }
    }

    fun getMyQuestions(): Flow<PagingData<QuestionContent>> =
        Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { QuestionPagingSource(questionRepository) }
        ).flow.cachedIn(viewModelScope)

    fun getLikedQuestions(): Flow<PagingData<QuestionContent>> =
        Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { QuestionPagingSource(questionRepository, likeFilter = true)}
        ).flow.cachedIn(viewModelScope)

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