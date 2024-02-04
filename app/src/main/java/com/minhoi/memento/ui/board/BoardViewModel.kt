package com.minhoi.memento.ui.board

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.minhoi.memento.repository.BoardRepository

class BoardViewModel() : ViewModel() {
    private val boardRepository = BoardRepository()

    val boardResponse = boardRepository.getMenteeBoardsStream(5).cachedIn(viewModelScope)
}