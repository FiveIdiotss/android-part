package com.minhoi.memento.ui.question

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.minhoi.memento.data.dto.question.QuestionContent
import com.minhoi.memento.repository.QuestionPagingSource
import com.minhoi.memento.repository.QuestionRepository
import kotlinx.coroutines.flow.Flow

class QuestionViewModel : ViewModel() {

    fun getQuestions(): Flow<PagingData<QuestionContent>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { QuestionPagingSource(QuestionRepository()) })
            .flow
            .cachedIn(viewModelScope)
    }

}