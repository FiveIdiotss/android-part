package com.minhoi.memento.ui.mypage.received

import android.os.Build
import android.view.View
import androidx.activity.viewModels
import com.minhoi.memento.R
import com.minhoi.memento.base.BaseActivity
import com.minhoi.memento.data.dto.MentoringReceivedDto
import com.minhoi.memento.databinding.ActivityReceivedContentBinding
import com.minhoi.memento.ui.mypage.MypageViewModel
import com.minhoi.memento.utils.ProgressDialog
import com.minhoi.memento.utils.setOnSingleClickListener

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

        binding.receivedDto = receivedContent

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
    }
}