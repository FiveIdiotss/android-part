package com.minhoi.memento.ui.mypage

import android.content.Intent
import androidx.fragment.app.viewModels
import com.minhoi.memento.R
import com.minhoi.memento.base.BaseFragment
import com.minhoi.memento.databinding.FragmentMypageBinding
import com.minhoi.memento.utils.setOnSingleClickListener

class MypageFragment : BaseFragment<FragmentMypageBinding>() {

    override val layoutResourceId: Int = R.layout.fragment_mypage
    private val viewModel by viewModels<MypageViewModel>()

    override fun initView() {

        val member = viewModel.getMemberInfo()

        binding.apply {
            this.member = member
            lifecycleOwner = this@MypageFragment

            applyListBtn.setOnSingleClickListener {
                startActivity(Intent(requireContext(), ApplyListActivity::class.java).apply {
                    putExtra("requestType", "APPLY")
                })
            }
            appliedListBtn.setOnSingleClickListener {
                startActivity(Intent(requireContext(), ApplyListActivity::class.java).apply {
                    putExtra("requestType", "RECEIVE")
                })
            }
            userInfoLayout.setOnSingleClickListener {
                startActivity(Intent(requireContext(), EditProfileActivity::class.java))
            }
            myMentorBtn.setOnSingleClickListener {
                startActivity(Intent(requireContext(), MyMentoringActivity::class.java))
            }
            myPostsBtn.setOnSingleClickListener {
                startActivity(Intent(requireContext(), MyPostsActivity::class.java))
            }
            bookmarkBoardsBtn.setOnSingleClickListener {
                startActivity(Intent(requireContext(), BookmarkBoardListActivity::class.java))
            }
        }
    }

}