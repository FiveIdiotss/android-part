package com.minhoi.memento.ui.board

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.minhoi.memento.R
import com.minhoi.memento.base.BaseFragment
import com.minhoi.memento.databinding.FragmentApplyShowResultBinding
import com.minhoi.memento.ui.UiState
import com.minhoi.memento.utils.hideLoading
import com.minhoi.memento.utils.setOnSingleClickListener
import com.minhoi.memento.utils.showLoading
import com.minhoi.memento.utils.showToast
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ApplyShowResultFragment : BaseFragment<FragmentApplyShowResultBinding>() {
    override val layoutResourceId: Int = R.layout.fragment_apply_show_result
    private lateinit var viewModel: BoardViewModel

    override fun initView() {
        viewModel = ViewModelProvider(requireActivity())[BoardViewModel::class.java]
        binding.viewModel = viewModel

        binding.applyBtn.setOnSingleClickListener {
            viewModel.applyMentoring()
        }
        observeApplyState()
    }
    private fun observeApplyState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.applyState.collectLatest {
                    when (it) {
                        is UiState.Empty -> {}
                        is UiState.Loading -> {
                            childFragmentManager.showLoading()
                        }
                        is UiState.Success -> {
                            childFragmentManager.hideLoading()
                            requireContext().showToast("멘토링 신청이 완료되었습니다.")
                        }
                        is UiState.Error -> {
                            childFragmentManager.hideLoading()
                            requireContext().showToast("네트워크 오류가 발생하였습니다. 다시 시도해주세요.")
                        }
                    }
                }
            }
        }
    }
}