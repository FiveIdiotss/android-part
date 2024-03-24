package com.minhoi.memento.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minhoi.memento.MentoApplication
import com.minhoi.memento.data.dto.BoardContentDto
import com.minhoi.memento.data.dto.chat.ChatRoom
import com.minhoi.memento.repository.BoardRepository
import com.minhoi.memento.repository.ChatRepository
import com.minhoi.memento.repository.MemberRepository
import com.minhoi.memento.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

class HomeViewModel : ViewModel() {
    private val boardRepository = BoardRepository()
    private val chatRepository = ChatRepository()
    private val memberRepository = MemberRepository()

    private val member = MentoApplication.memberPrefs.getMemberPrefs()

    private val _menteeBoardContents = MutableLiveData<List<BoardContentDto>>()
    val menteeBoardContent: LiveData<List<BoardContentDto>> = _menteeBoardContents

    private val _chatRooms = MutableStateFlow<UiState<List<ChatRoom>>>(UiState.Loading)
    val chatRooms: StateFlow<UiState<List<ChatRoom>>> = _chatRooms.asStateFlow()

    init {
        getPreviewBoards()
        getChatRooms()
    }

    fun getPreviewBoards() {
        viewModelScope.launch {
            val response = boardRepository.getPreviewBoards()
            if (response.isSuccessful) {
                _menteeBoardContents.value = response.body()?.content
                Log.d("HOMEVIEWMODEL", "getMenteeBoards: ${response.body()}")
            }
        }
    }

    fun getChatRooms() {
        viewModelScope.launch {
            chatRepository.getChatRooms(member.id).collectLatest { result ->
                result.handleResponse(
                    onSuccess = { chatRooms ->
                        Log.d("HomeViewModel", "getChatRooms: $chatRooms")
                        _chatRooms.update { UiState.Success(chatRooms) }
                    },
                    onError = { error ->
                        _chatRooms.update { UiState.Error(Throwable(error)) }
                    }
                )
            }
        }
    }

    private suspend fun getMemberInfoAsFlow(memberId: Long) = flow {
        val response = memberRepository.getMemberInfo(memberId)
        if (response.isSuccessful) {
            emit(response.body() ?: throw Exception("MemberInfo is null"))
        } else {
            throw Exception("Network error: ${response.code()}")
        }
    }
}