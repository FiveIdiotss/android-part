package com.minhoi.memento.ui.mypage

import android.content.Intent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.minhoi.memento.R
import com.minhoi.memento.adapter.MatchedMentoringAdapter
import com.minhoi.memento.base.BaseActivity
import com.minhoi.memento.data.dto.MemberDTO
import com.minhoi.memento.data.dto.MentoringMatchInfo
import com.minhoi.memento.databinding.ActivityMyMentoringBinding
import com.minhoi.memento.ui.chat.ChatActivity
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class MyMentoringActivity : BaseActivity<ActivityMyMentoringBinding>() {
    private val TAG = MyMentoringActivity::class.java.simpleName
    override val layoutResourceId: Int = R.layout.activity_my_mentoring

    private val viewModel by viewModels<MypageViewModel>()
    private val matchedMentoringAdapter: MatchedMentoringAdapter by lazy {
        MatchedMentoringAdapter {
            // 채팅 버튼 클릭시 상대방의 id를 ChatActivity로 전달
            startActivity(Intent(this, ChatActivity::class.java).apply {
                putExtra("receiverId", it.menteeMemberId)
                putExtra("receiverName", it.menteeMemberName)
            })
        }
    }

    override fun initView() {

        lifecycleScope.launch {
            viewModel.getMatchedMentoring()
        }

        binding.myMentoringRv.apply {
            adapter = matchedMentoringAdapter
            layoutManager = LinearLayoutManager(this@MyMentoringActivity, LinearLayoutManager.VERTICAL, false)
        }

        viewModel.matchedMentoringList.observe(this) { matchedMentoringList ->
            val matchedMembers = mutableListOf<Pair<MentoringMatchInfo, MemberDTO>>()
            // 매칭된 멘토링 목록이 생성되면 멘토링 신청자의 정보를 가져옴
            lifecycleScope.launch {
                val deferredList = matchedMentoringList.map { mentoringMatchInfo ->
                    async {
                        val member = viewModel.getOtherMemberInfo(mentoringMatchInfo.menteeMemberId)
                        mentoringMatchInfo to member
                    }
                }

                val results = deferredList.awaitAll()
                matchedMembers.addAll(results.filter { it.second != null }.map { it.first to it.second!! })
                matchedMentoringAdapter.setList(matchedMembers)
            }
        }
    }

}