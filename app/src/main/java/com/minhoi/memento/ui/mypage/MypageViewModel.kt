package com.minhoi.memento.ui.mypage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minhoi.memento.MentoApplication
import com.minhoi.memento.data.dto.MentoringApplyDto
import com.minhoi.memento.repository.MemberRepository
import kotlinx.coroutines.launch

class MypageViewModel : ViewModel() {

    private val memberRepository = MemberRepository()
    private val member = MentoApplication.memberPrefs.getMemberPrefs()

    private val _applyList = MutableLiveData<List<MentoringApplyDto>>()
    val applyList: LiveData<List<MentoringApplyDto>> = _applyList

    private val _applyContent = MutableLiveData<MentoringApplyDto>()
    val applyContent: LiveData<MentoringApplyDto> = _applyContent

    fun getApplyList() {
        viewModelScope.launch {
            member.let { member ->
                val response = memberRepository.getApplyList(member.id)
                if (response.isSuccessful) {
                    _applyList.value = response.body()
                    Log.d("ApplyList", "getApplyListSuccess: ${response.body()}")
                } else {
                    Log.d("ApplyList", "getApplyListFailed: ${response.code()}")
                }
            }
        }
    }

    fun selectApplyContent(applyContent: MentoringApplyDto) {
        _applyContent.value = applyContent
    }
}