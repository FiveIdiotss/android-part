package com.minhoi.memento.ui.mypage

import android.content.Intent
import androidx.fragment.app.viewModels
import com.minhoi.memento.R
import com.minhoi.memento.base.BaseFragment
import com.minhoi.memento.databinding.FragmentMypageBinding
import com.minhoi.memento.ui.login.LoginActivity
import com.minhoi.memento.ui.question.QuestionListActivity
import com.minhoi.memento.utils.repeatOnStarted
import com.minhoi.memento.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MypageFragment : BaseFragment<FragmentMypageBinding>() {

    override val layoutResourceId: Int = R.layout.fragment_mypage
    private val viewModel by viewModels<MypageViewModel>()

    override fun initView() {
        observeMemberInfo()
        observeSignOutEvent()

        binding.apply {
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
            notificationSettingBtn.setOnSingleClickListener {
                startActivity(Intent(requireContext(), NotificationSettingActivity::class.java))
            }
            myQuestionBtn.setOnSingleClickListener {
                startActivity(Intent(requireContext(), QuestionListActivity::class.java).putExtra("type", "myQuestion"))
            }
            likeQuestionsBtn.setOnSingleClickListener {
                startActivity(Intent(requireContext(), QuestionListActivity::class.java).putExtra("type", "likeQuestion"))
            }
            signOutBtn.setOnSingleClickListener {
                viewModel.signOut()
            }
        }
    }

    private fun observeMemberInfo() {
        viewModel.memberInfo.observe(viewLifecycleOwner) {
            binding.member = it
        }
    }

    private fun observeSignOutEvent() {
        repeatOnStarted {
            viewModel.signOutEvent.collect {
                if (it) {
                    startActivity(Intent(requireActivity(), LoginActivity::class.java))
                    requireActivity().finish()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getMemberInfo()
    }
}