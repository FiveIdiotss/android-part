package com.minhoi.memento.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minhoi.memento.MentoApplication
import com.minhoi.memento.data.dto.BoardContentDto
import com.minhoi.memento.data.dto.MemberDTO
import com.minhoi.memento.data.dto.chat.ChatRoom
import com.minhoi.memento.repository.board.BoardRepository
import com.minhoi.memento.repository.chat.ChatRepository
import com.minhoi.memento.repository.login.LoginRepository
import com.minhoi.memento.repository.login.LoginRepositoryImpl
import com.minhoi.memento.repository.member.MemberRepository
import com.minhoi.memento.repository.member.MemberRepositoryImpl
import com.minhoi.memento.repository.question.QuestionRepository
import com.minhoi.memento.repository.question.QuestionRepositoryImpl
import com.minhoi.memento.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val boardRepository: BoardRepository,
    private val chatRepository: ChatRepository,
    private val memberRepository: MemberRepository,
    private val loginRepository: LoginRepository,
    private val questionRepository: QuestionRepository,
) : ViewModel() {

    private val member = MentoApplication.memberPrefs.getMemberPrefs()

    private val _menteeBoardContents = MutableLiveData<List<BoardContentDto>>()
    val menteeBoardContent: LiveData<List<BoardContentDto>> = _menteeBoardContents

    private val _chatRooms = MutableStateFlow<UiState<List<Pair<ChatRoom, MemberDTO>>>>(UiState.Empty)
    val chatRooms: StateFlow<UiState<List<Pair<ChatRoom, MemberDTO>>>> = _chatRooms.asStateFlow()

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

    /*
    * 채팅방 목록을 가져오고, 채팅방 목록에 대한 멤버 정보를 가져와서 Pair로 묶어서 UI에 전달하는 함수
     */
    @OptIn(FlowPreview::class)
    fun getChatRooms() {
        val chatRoomsWithMember = mutableListOf<Pair<ChatRoom, MemberDTO>>()
        viewModelScope.launch {
            _chatRooms.update { UiState.Loading }
            chatRepository.getChatRooms(member.id).collectLatest { result ->
                result.handleResponse(
                    onSuccess = { chatRooms ->
                        launch {
                            // flatMapConcat을 사용하여 순차적으로 멤버 정보를 가져오고 temp 리스트에 추가
                            chatRooms.asFlow().flatMapConcat { chatRoom ->
                                getMemberInfoAsFlow(chatRoom.receiverId).map { member ->
                                    chatRoom to member
                                }
                            }
                                .catch { e -> _chatRooms.update { UiState.Error(e) } }
                                .collect { pair -> chatRoomsWithMember.add(pair) }
                            // 모든 멤버 정보가 temp 리스트에 추가된 후 UI 상태 업데이트 (flatMapConcat 사용 시 순차적으로 실행)
                            _chatRooms.update { UiState.Success(chatRoomsWithMember) }
                            Log.d("HomeViewModel", "getChatRooms: $chatRoomsWithMember")
                        }
                    },
                    onError = { error ->
                        _chatRooms.update { UiState.Error(error.exception) }
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