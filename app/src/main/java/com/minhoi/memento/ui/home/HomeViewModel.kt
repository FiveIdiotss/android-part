package com.minhoi.memento.ui.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.minhoi.memento.MentoApplication
import com.minhoi.memento.data.dto.BoardContentDto
import com.minhoi.memento.data.dto.MemberDTO
import com.minhoi.memento.data.dto.chat.ChatRoom
import com.minhoi.memento.data.dto.notification.NotificationListDto
import com.minhoi.memento.data.dto.question.QuestionContent
import com.minhoi.memento.data.network.socket.StompManager
import com.minhoi.memento.repository.board.BoardRepository
import com.minhoi.memento.repository.chat.ChatRepository
import com.minhoi.memento.repository.member.MemberRepository
import com.minhoi.memento.repository.pagingsource.BoardPagingSource
import com.minhoi.memento.repository.question.QuestionRepository
import com.minhoi.memento.ui.UiState
import com.minhoi.memento.utils.toRelativeTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val boardRepository: BoardRepository,
    private val chatRepository: ChatRepository,
    private val memberRepository: MemberRepository,
    private val questionRepository: QuestionRepository,
) : ViewModel() {

    private val member = MentoApplication.memberPrefs.getMemberPrefs()

    private val _questionPreviewContents = MutableLiveData<List<QuestionContent>>()
    val questionPreviewContents: LiveData<List<QuestionContent>> = _questionPreviewContents

    private val _chatRooms = MutableStateFlow<UiState<List<Pair<ChatRoom, MemberDTO>>>>(UiState.Empty)
    val chatRooms: StateFlow<UiState<List<Pair<ChatRoom, MemberDTO>>>> = _chatRooms.asStateFlow()

    private val _loginState = MutableStateFlow<UiState<Boolean>>(UiState.Loading)
    val loginState: StateFlow<UiState<Boolean>> = _loginState.asStateFlow()

    private val _notificationList = MutableStateFlow<UiState<List<NotificationListDto>>>(UiState.Empty)
    val notificationList: StateFlow<UiState<List<NotificationListDto>>> = _notificationList.asStateFlow()

    private val _notificationUnreadCount = MutableStateFlow<Int>(0)
    val notificationUnreadCount: StateFlow<Int> = _notificationUnreadCount.asStateFlow()

    private val _chatUnreadCount = MutableStateFlow<Int>(0)

    private var currentPage: Int = 1
    var isLastPage: Boolean = false
        private set
    private val notificationTemp = mutableListOf<NotificationListDto>()

    init {
        StompManager.connectToSocket()
        getPreviewBoards()
        getPreviewQuestions()
        getUnreadNotificationCount()
        subscribeNotification()
    }

    fun getPreviewBoards(): Flow<PagingData<BoardContentDto>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { BoardPagingSource(boardRepository) })
            .flow
            .cachedIn(viewModelScope)
    }

    fun getPreviewQuestions() {
        viewModelScope.launch {
            questionRepository.getQuestions(1, 7).collectLatest {
                it.handleResponse(
                    onSuccess = {
                        _questionPreviewContents.value = it.data.content
                    },
                    onError = { error ->

                    }
                )
            }
        }
    }

    /** 채팅방마다 구독하여 읽지 않은 메세지의 개수를 실시간으로 전달받는 함수
    채팅방 목록 성공적으로 가져오고 구독 */
    @SuppressLint("CheckResult")
    fun subscribeChatRooms(rooms: List<ChatRoom>) {
        rooms.forEach { chatRoom ->
            StompManager.stompClient?.topic("/sub/unreadCount/${chatRoom.id}")
                ?.subscribe { message ->
                    val data = JSONObject(message.payload)
                    val unreadMessageCount = data.getInt("unreadMessageCount")
                    Log.d(
                        "HomeViewModel",
                        "subscribeChatRooms: ${chatRoom.id}  ${unreadMessageCount}"
                    )
                    val currentState = _chatRooms.value
                    if (currentState is UiState.Success) {
                        val updatedList = currentState.data.map { pair ->
                            if (pair.first.id == chatRoom.id) {
                                pair.first.copy(unreadMessageCount = unreadMessageCount) to pair.second
                            } else {
                                pair
                            }
                        }
                        _chatRooms.update { UiState.Success(updatedList) }
                    }
                }
        }
    }

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
                            chatRooms.data.asFlow().flatMapConcat { chatRoom ->
                                getMemberInfoAsFlow(chatRoom.receiverId).map { member ->
                                    chatRoom to member.data
                                }
                            }
                                .catch { e -> _chatRooms.update { UiState.Error(e) } }
                                .collect { pair -> chatRoomsWithMember.add(pair) }
                            // 모든 멤버 정보가 temp 리스트에 추가된 후 UI 상태 업데이트 (flatMapConcat 사용 시 순차적으로 실행)
                            subscribeChatRooms(chatRooms.data)
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

    fun getUnreadNotificationCount() {
        viewModelScope.launch {
            memberRepository.getUnreadNotificationCounts().collectLatest {
                it.handleResponse(
                    onSuccess = { value ->
                        _notificationUnreadCount.update { value.data }
                    },
                    onError = {
                        _notificationUnreadCount.update { -1 }
                    }
                )
            }
        }
    }

    fun getNotificationList() {
        viewModelScope.launch {
            memberRepository.getNotificationList(currentPage, 5)
                .onStart { _notificationList.update { UiState.Loading } }
                .collectLatest {
                    it.handleResponse(
                        onSuccess = { result ->
                            if (result.data.pageInfo.totalPages == currentPage) isLastPage = true
                            currentPage++

                            val notifications = result.data.content.map { data ->
                                data.copy(arriveTime = data.arriveTime.toRelativeTime())
                            }
                            notificationTemp.addAll(notifications)
                            _notificationList.update { UiState.Success(notificationTemp) }
                        },
                        onError = { error ->
                            _notificationList.update { UiState.Error(error.exception) }
                        }
                    )
                }
        }
    }

    @SuppressLint("CheckResult")
    fun subscribeNotification() {
        StompManager.stompClient?.topic("/sub/notifications/${member.id}")
            ?.subscribe { message ->
                val unreadNotificationCount = message.payload.toInt()
                Log.d("HomeViewModel", "subscribeNotification: $unreadNotificationCount")
                _notificationUnreadCount.update { unreadNotificationCount }
            }
    }


    private fun getMemberInfoAsFlow(memberId: Long) = flow {
        val response = memberRepository.getMemberInfo(memberId)
        if (response.isSuccessful) {
            emit(response.body() ?: throw Exception("MemberInfo is null"))
        } else {
            throw Exception("Network error: ${response.code()}")
        }
    }

    fun saveFCMToken(token: String) {
        memberRepository.saveFCMToken(token).enqueue(object: Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    Log.d("HomeViewModel", "initView: success")

                } else {

                }
            }
            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d("HomeViewModel", "initView: failed")

            }
        })
    }

    override fun onCleared() {
        super.onCleared()
        StompManager.disconnect()
        Log.d("HomeViewModel", "onCleared: OnCleared!!")
    }
}