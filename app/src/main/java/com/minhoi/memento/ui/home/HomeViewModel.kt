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
import androidx.paging.filter
import androidx.paging.map
import com.minhoi.memento.MentoApplication
import com.minhoi.memento.data.dto.BoardContentDto
import com.minhoi.memento.data.dto.MemberDTO
import com.minhoi.memento.data.dto.chat.ChatRoom
import com.minhoi.memento.data.dto.chat.LatestMessageDto
import com.minhoi.memento.data.dto.notification.NotificationListDto
import com.minhoi.memento.data.dto.question.QuestionContent
import com.minhoi.memento.data.network.socket.StompManager
import com.minhoi.memento.repository.board.BoardRepository
import com.minhoi.memento.repository.chat.ChatRepository
import com.minhoi.memento.repository.member.MemberRepository
import com.minhoi.memento.repository.pagingsource.BoardPagingSource
import com.minhoi.memento.repository.pagingsource.NotificationPagingSource
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

    private val _notifications = MutableStateFlow<PagingData<NotificationListDto>>(PagingData.empty())
    val notifications: StateFlow<PagingData<NotificationListDto>> = _notifications.asStateFlow()

    private val _expandState = MutableStateFlow<Boolean>(false)
    val expandState: StateFlow<Boolean> = _expandState.asStateFlow()

    private val _notificationUnreadCount = MutableStateFlow<Int>(0)
    val notificationUnreadCount: StateFlow<Int> = _notificationUnreadCount.asStateFlow()

    private val _chatUnreadCount = MutableStateFlow<Int>(0)
    val chatUnreadCount: StateFlow<Int> = _chatUnreadCount.asStateFlow()

    init {
        StompManager.connectToSocket()
        getPreviewBoards()
        getPreviewQuestions()
        getUnreadNotificationCount()
        subscribeNotification()
    }

    fun getNotifications() = viewModelScope.launch {
        Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { NotificationPagingSource(memberRepository) }
        ).flow.cachedIn(viewModelScope)
            .map { pagingData ->
                pagingData.map { notification ->
                    notification.copy(arriveTime = notification.arriveTime.toRelativeTime())
                }
            }.collectLatest { data ->
                _notifications.value = data
            }
    }

    /** 알림 목록에서 x버튼 활성화하는 함수 */
    fun expandNotifications() {
        val expandedData = _notifications.value.map {
            it.copy(isExpanded = true)
        }
        _notifications.update { expandedData }
        _expandState.update { true }
    }

    fun unExpandNotifications() {
        val expandedData = _notifications.value.map {
            it.copy(isExpanded = false)
        }
        _notifications.update { expandedData }
        _expandState.update { false }
    }

    fun deleteNotification(notificationId: Long) {
        viewModelScope.launch {
            val filteredData = _notifications.value.filter { it.id != notificationId }
            _notifications.update { filteredData }
            memberRepository.deleteNotification(notificationId).collectLatest {
                it.handleResponse(
                    onSuccess = {},
                    onError = {}
                )
            }
        }
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
            questionRepository.getQuestions(1, 7, false, null, null).collectLatest {
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
                    val data = message.payload
                    val s = JSONObject(data)
                    val count = s.getInt("unreadMessageCount")
                    val latestMessageResponse = s.getJSONObject("latestMessageDTO")
                    val content = latestMessageResponse.getString("content")
                    val sendTime = latestMessageResponse.getString("localDateTime")

                    _chatUnreadCount.update { count }
                    val currentState = _chatRooms.value
                    if (currentState is UiState.Success) {
                        val updatedList = currentState.data.map { pair ->
                            if (pair.first.id == chatRoom.id) {
                                pair.first.copy(
                                    unreadMessageCount = count,
                                    latestMessage = LatestMessageDto(content, sendTime)
                                ) to pair.second
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
            chatRepository.getChatRooms().collectLatest { result ->
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

    private fun getUnreadNotificationCount() {
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

    fun resetUnreadNotificationCount() {
        _notificationUnreadCount.update { 0 }
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