package com.minhoi.memento.ui.mypage.received

import android.os.Build
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.minhoi.memento.R
import com.minhoi.memento.base.BaseActivity
import com.minhoi.memento.data.dto.MentoringReceivedDto
import com.minhoi.memento.databinding.ActivityReceivedContentBinding
import com.minhoi.memento.ui.UiState
import com.minhoi.memento.ui.mypage.MypageViewModel
import com.minhoi.memento.utils.hideLoading
import com.minhoi.memento.utils.setOnSingleClickListener
import com.minhoi.memento.utils.showLoading
import com.minhoi.memento.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReceivedContentActivity : BaseActivity<ActivityReceivedContentBinding>() {
    override val layoutResourceId: Int = R.layout.activity_received_content
    private val viewModel: MypageViewModel by viewModels()

    override fun initView() {
        val intent = intent

        val receivedContent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("receivedDto", MentoringReceivedDto::class.java)
        } else {
            intent.getParcelableExtra("receivedDto") as? MentoringReceivedDto
        }

        if (receivedContent != null) {
            binding.receivedDto = receivedContent
            viewModel.getApplyInfo(receivedContent.applyId)
        } else {
            showToast("일시적인 오류가 발생하였습니다. 다시 시도해주세요")
        }

        // 신청서 상태에 따라 버튼 노출
        when (receivedContent?.applyState) {
            "HOLDING" -> {
                binding.acceptOrDenyLayout.visibility = View.VISIBLE
                binding.alreadyProcessedText.visibility = View.GONE
            }
            "COMPLETE" -> {
                binding.alreadyProcessedText.visibility = View.VISIBLE
                binding.acceptOrDenyLayout.visibility = View.GONE
            }
        }

        binding.apply {
            acceptBtn.setOnSingleClickListener {
                viewModel.acceptApply(receivedContent!!.applyId)
            }

            rejectBtn.setOnSingleClickListener {
                viewModel.rejectApply(receivedContent!!.applyId)
            }
        }

        observeAcceptState()
        observeRejectState()
        observeApplyContent()
    }

    private fun observeAcceptState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.acceptState.collectLatest { state ->
                    branchState(state)
                }
            }
        }
    }

    private fun observeRejectState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.rejectState.collectLatest { state ->
                    branchState(state)
                }
            }
        }
    }

    private fun observeApplyContent() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.applyContent.collectLatest {
                    when (it) {
                        is UiState.Empty -> {}
                        is UiState.Loading -> {
                            supportFragmentManager.showLoading()
                        }
                        is UiState.Success -> {
                            supportFragmentManager.hideLoading()
                            binding.applyInfo = it.data
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

    private fun branchState(state: UiState<Boolean>) {
        when (state) {
            // do nothing
            is UiState.Empty -> {}

            is UiState.Success -> {
                showToast("수락 완료")
                supportFragmentManager.hideLoading()
                finish()
            }
            is UiState.Loading -> {
                supportFragmentManager.showLoading()
            }
            is UiState.Error -> {
                supportFragmentManager.hideLoading()
                showToast("일시적인 오류가 발생하였습니다. 다시 시도해주세요")
                finish()
            }
        }
    }

}