package com.minhoi.memento.ui.mypage.mentoring

import android.content.Intent
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.minhoi.memento.R
import com.minhoi.memento.base.BaseActivity
import com.minhoi.memento.data.dto.member.MemberDTO
import com.minhoi.memento.data.dto.mentoring.MentoringMatchInfo
import com.minhoi.memento.databinding.ActivityMyMentoringBinding
import com.minhoi.memento.ui.UiState
import com.minhoi.memento.ui.adapter.MatchedMentoringAdapter
import com.minhoi.memento.ui.chat.ChatActivity
import com.minhoi.memento.ui.mypage.MypageViewModel
import com.minhoi.memento.utils.hideLoading
import com.minhoi.memento.utils.showLoading
import com.minhoi.memento.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyMentoringActivity : BaseActivity<ActivityMyMentoringBinding>() {
    private val TAG = MyMentoringActivity::class.java.simpleName
    override val layoutResourceId: Int = R.layout.activity_my_mentoring

    private val viewModel by viewModels<MypageViewModel>()
    private val mentorAdapter: MatchedMentoringAdapter by lazy {
        MatchedMentoringAdapter { mentorInfo ->
            startChatActivity(mentorInfo.otherMemberId, mentorInfo.otherMemberName)
        }
    }

    private val menteeAdapter: MatchedMentoringAdapter by lazy {
        MatchedMentoringAdapter { menteeInfo ->
            startChatActivity(menteeInfo.otherMemberId, menteeInfo.otherMemberName)
        }
    }

    override fun initView() {
        setupToolbar("내 멘토/멘티")

        viewModel.getMentorInfo()
        viewModel.getMenteeInfo()

        binding.myMentorRv.apply {
            adapter = mentorAdapter
            layoutManager =
                LinearLayoutManager(this@MyMentoringActivity, LinearLayoutManager.VERTICAL, false)
        }
        binding.myMenteeRv.apply {
            adapter = menteeAdapter
            layoutManager =
                LinearLayoutManager(this@MyMentoringActivity, LinearLayoutManager.VERTICAL, false)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.mentorInfo.collectLatest {
                    when (it) {
                        is UiState.Empty -> {}
                        is UiState.Loading -> {
                            supportFragmentManager.showLoading()
                        }

                        is UiState.Success -> {
                            supportFragmentManager.hideLoading()
                            // 매칭된 멘토링 목록이 생성되면 멘토링 신청자의 정보를 가져옴
                            try {
                                val matchedMembers = getOtherMemberInfo(it.data)
                                mentorAdapter.setList(matchedMembers)
                            } catch (e: Exception) {
                                showToast(e.message.toString())
                            }
                        }
                        is UiState.Error -> {
                            supportFragmentManager.hideLoading()
                            showToast(it.error?.message.toString())
                        }
                    }
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.menteeInfo.collectLatest {
                    when (it) {
                        is UiState.Empty -> {}
                        is UiState.Loading -> {
                            supportFragmentManager.showLoading()
                        }
                        is UiState.Success -> {
                            supportFragmentManager.hideLoading()
                            // 매칭된 멘토링 목록이 생성되면 멘토링 신청자의 정보를 가져옴
                            try {
                                val matchedMembers = getOtherMemberInfo(it.data)
                                menteeAdapter.setList(matchedMembers)
                            } catch (e: Exception) {
                                showToast(e.message.toString())
                            }
                        }
                        is UiState.Error -> {
                            supportFragmentManager.hideLoading()
                            showToast(it.error?.message.toString())
                        }
                    }
                }
            }
        }
    }

    private suspend fun getOtherMemberInfo(list: List<MentoringMatchInfo>): List<Pair<MentoringMatchInfo, MemberDTO>> {
        return list.map { mentoringMatchInfo ->
            // 동기적으로 사용자 정보를 가져오기
            val member = viewModel.getOtherMemberInfo(mentoringMatchInfo.otherMemberId)
            mentoringMatchInfo to member!!
        }
    }

    private fun startChatActivity(otherMemberId: Long, otherMemberName: String) {
        startActivity(Intent(this@MyMentoringActivity, ChatActivity::class.java).apply {
            putExtra("receiverId", otherMemberId)
            putExtra("receiverName", otherMemberName)
        })
    }
}

