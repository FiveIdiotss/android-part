package com.minhoi.memento.ui.board

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.minhoi.memento.data.dto.BoardContentResponse
import com.minhoi.memento.repository.BoardRepository
import kotlinx.coroutines.launch

class BoardViewModel() : ViewModel() {
    private val boardRepository = BoardRepository()

    private val _post = MutableLiveData<BoardContentResponse>()
    val post: LiveData<BoardContentResponse> = _post

    val getBoardsStream = boardRepository.getMenteeBoardsStream(5).cachedIn(viewModelScope)

    fun getBoardContent(boardId: Long) {
        viewModelScope.launch {
            val response = boardRepository.getBoardContent(boardId)
            if (response.isSuccessful) {
                _post.value = response.body()?.boardDTO
            }
        }
    }
}