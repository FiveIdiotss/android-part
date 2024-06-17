package com.minhoi.memento.ui.board

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.minhoi.memento.MentoApplication
import com.minhoi.memento.data.dto.BoardContentResponse
import com.minhoi.memento.data.dto.BoardImage
import com.minhoi.memento.data.dto.MentorBoardPostDto
import com.minhoi.memento.data.dto.MentoringApplyRequest
import com.minhoi.memento.data.dto.TimeTableDto
import com.minhoi.memento.data.model.DayOfWeek
import com.minhoi.memento.repository.board.BoardRepository
import com.minhoi.memento.repository.member.MemberRepository
import com.minhoi.memento.ui.UiState
import com.minhoi.memento.utils.FileManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class BoardViewModel @Inject constructor(
    private val boardRepository: BoardRepository,
    private val memberRepository: MemberRepository,
    private val fileManager: FileManager
) : ViewModel() {

    private val member = MentoApplication.memberPrefs.getMemberPrefs()

    private val _post = MutableLiveData<BoardContentResponse>()
    val post: LiveData<BoardContentResponse> = _post

    private val _boardContent = MutableStateFlow<UiState<BoardContentResponse>>(UiState.Loading)
    val boardContent: StateFlow<UiState<BoardContentResponse>> = _boardContent.asStateFlow()

    private val _isAvailableDay = MutableLiveData<Boolean>()
    val isAvailableDay: LiveData<Boolean> = _isAvailableDay

    private val _boardBookmarkState = MutableLiveData<Boolean>()
    val boardBookmarkState: LiveData<Boolean> = _boardBookmarkState

    private val _applyState = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val applyState: StateFlow<UiState<Boolean>> = _applyState.asStateFlow()

    private val _isMyPost = MutableStateFlow<Boolean>(false)
    val isMyPost: StateFlow<Boolean> = _isMyPost.asStateFlow()

    private val _postImages = MutableStateFlow<List<Uri>>(emptyList())
    val postImages: StateFlow<List<Uri>> = _postImages.asStateFlow()

    private val _postTitle = MutableStateFlow("")
    val postTitle: StateFlow<String> = _postTitle

    private val _postIntroduce = MutableStateFlow("")
    val postIntroduce: StateFlow<String> = _postIntroduce

    private val _postTarget = MutableStateFlow("")
    val postTarget: StateFlow<String> = _postTarget

    private val _postDescription = MutableStateFlow("")
    val postDescription: StateFlow<String> = _postDescription

    private val _postConsultTime = MutableStateFlow<Int>(0)
    val postConsultTime: StateFlow<Int> = _postConsultTime

    private val _postCategory = MutableStateFlow("")
    val postCategory: StateFlow<String> = _postCategory

    private val _postTimeTable = MutableStateFlow<List<TimeTableDto>>(emptyList())
    val postTimeTable: StateFlow<List<TimeTableDto>> = _postTimeTable

    private val _postAvailableDays = MutableStateFlow<List<DayOfWeek>>(emptyList())
    val postAvailableDays: StateFlow<List<DayOfWeek>> = _postAvailableDays

    var selectedDate: String = ""
        private set
    var selectedTime: String = ""
        private set
    var mentoringApplyMessage: String = ""
        private set

    init {
        _isAvailableDay.value = false
    }

    fun getBoardContent(boardId: Long) {
        viewModelScope.launch {
            boardRepository.getBoardContent(boardId).collectLatest {
                it.handleResponse(
                    onSuccess = { value ->
                        _post.value = value.data
                        if (value.data.boardImageUrls.isEmpty()) {
                            val emptyImageData =
                                value.data.copy(boardImageUrls = listOf(BoardImage("https://picsum.photos/300")))
                            _boardContent.update { UiState.Success(emptyImageData) }
                        } else {
                            _boardContent.update { UiState.Success(value.data) }
                        }
                        if (value.data.boardDTO.memberId == member.id) {
                            _isMyPost.update { true }
                        }
                    },
                    onError = { error ->
                        _boardContent.update { UiState.Error(error.exception) }
                    }
                )
            }
        }
    }

    fun setSelectedTime(time: String) {
        selectedTime = time
    }

    fun onDateSelected(year: Int, month: Int, day: Int) {
        val selectedDay = DayOfWeek.getDayOfWeek(year, month, day)
        selectedDate = LocalDate.of(year, month + 1, day).toString()
        selectedTime = ""
        val isAvailable = post.value?.availableDays?.contains(selectedDay) ?: false
        _isAvailableDay.value = isAvailable
    }

    fun applyMentoring() {
        viewModelScope.launch {
            boardRepository.applyMentoring(
                _post.value!!.boardDTO.boardId,
                MentoringApplyRequest(mentoringApplyMessage, selectedDate, selectedTime)
            ).collectLatest {
                it.handleResponse(
                    onSuccess = {
                        _applyState.update { UiState.Success(true) }
                    },
                    onError = { error ->
                        _applyState.update { UiState.Error(error.exception) }
                    }
                )
            }
        }
    }

    fun executeBookmark(boardId: Long, isBookmarked: Boolean) {
        viewModelScope.launch {
            when (isBookmarked) {
                true -> boardRepository.executeUnBookmark(boardId)
                false -> boardRepository.executeBookmark(boardId)
            }
        }
    }

    fun setBookmarkState(state: Boolean) {
        _boardBookmarkState.value = state
    }

    fun setMentoringMessage(message: String) {
        mentoringApplyMessage = message
    }

    fun postBoard() {
        viewModelScope.launch {
            val post = MentorBoardPostDto(
                _postTitle.value,
                _postIntroduce.value,
                _postTarget.value,
                _postDescription.value,
                _postConsultTime.value,
                _postCategory.value,
                _postTimeTable.value,
                _postAvailableDays.value
            )
            val postRequestBody = Gson().toJson(post).toRequestBody("application/json".toMediaTypeOrNull())
            val images =
                if (_postImages.value.isEmpty()) null
                else {
                    _postImages.value.map {
                        val fileType = fileManager.getFileMimeType(it)
                        fileManager.uriToMultipartBodyPart(it, fileType!!, "images")!!
                    }
                }
            Log.d(TAG, "postBoard: $post image : $images")
            boardRepository.postBoard(postRequestBody, images).collect {
                it.handleResponse(
                    onSuccess = {
                        Log.d(TAG, "deleteBoardPost: success")
                    },
                    onError = {
                        Log.d(TAG, "deleteBoardPost: ${it.exception!!.message}")
                    }
                )
            }
        }
    }

    fun setTitle(value: CharSequence) { _postTitle.value = value.toString() }
    fun setIntroduce(value: CharSequence) { _postIntroduce.value = value.toString() }
    fun setTarget(value: CharSequence) { _postTarget.value = value.toString() }
    fun setDescription(value: CharSequence) { _postDescription.value = value.toString() }
    fun setConsultTime(value: Int) { _postConsultTime.value = value }
    fun setCategory(value: CharSequence) { _postCategory.value = value.toString() }
    fun setMentorTimeTableList(value: TimeTableDto) { _postTimeTable.value += value }
    fun deleteMentorTimeTableAt(position: Int) {
        val currentTimetable = _postTimeTable.value.toMutableList()
        currentTimetable.removeAt(position)
        _postTimeTable.value = currentTimetable
    }

    fun changeConsultTime() {
        _postTimeTable.value = emptyList()
    }

    fun setSelectedCheckBoxes(day: DayOfWeek, isChecked: Boolean) {
        val currentList = _postAvailableDays.value.toMutableList()
        if (isChecked) {
            if (!currentList.contains(day)) {
                currentList.add(day)
            }
        } else {
            currentList.remove(day)
        }
        _postAvailableDays.value = currentList
    }

    fun addPostImages(image: List<Uri>) {
        _postImages.update { _postImages.value + image }
    }

    fun removePostImageAt(position: Int) {
        _postImages.update { postImages ->
            postImages.toMutableList().apply { removeAt(position) }
        }
    }
    companion object {
        private const val TAG = "BoardViewModel"
    }
}