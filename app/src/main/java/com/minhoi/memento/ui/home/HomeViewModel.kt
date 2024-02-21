package com.minhoi.memento.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minhoi.memento.data.dto.BoardContentDto
import com.minhoi.memento.repository.BoardRepository
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val boardRepository = BoardRepository()

    private var _menteeBoardContents = MutableLiveData<List<BoardContentDto>>()
    val menteeBoardContent: LiveData<List<BoardContentDto>> = _menteeBoardContents

    init {
        getPreviewBoards()
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

}