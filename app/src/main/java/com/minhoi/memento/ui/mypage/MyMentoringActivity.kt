package com.minhoi.memento.ui.mypage

import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.minhoi.memento.R
import com.minhoi.memento.adapter.MatchedMentoringAdapter
import com.minhoi.memento.base.BaseActivity
import com.minhoi.memento.data.dto.MemberDTO
import com.minhoi.memento.data.dto.MentoringMatchInfo
import com.minhoi.memento.databinding.ActivityMyMentoringBinding
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class MyMentoringActivity : BaseActivity<ActivityMyMentoringBinding>() {
    private val TAG = MyMentoringActivity::class.java.simpleName
    override val layoutResourceId: Int = R.layout.activity_my_mentoring

    private val viewModel by viewModels<MypageViewModel>()
    private val matchedMentoringAdapter: MatchedMentoringAdapter by lazy { MatchedMentoringAdapter() }

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
                        val member = viewModel.getMemberInfo(mentoringMatchInfo.menteeMemberId)
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