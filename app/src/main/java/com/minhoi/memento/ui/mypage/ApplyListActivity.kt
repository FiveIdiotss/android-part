package com.minhoi.memento.ui.mypage

import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.minhoi.memento.R
import com.minhoi.memento.adapter.ApplyListAdapter
import com.minhoi.memento.base.BaseActivity
import com.minhoi.memento.databinding.ActivityApplyListBinding
import com.minhoi.memento.ui.mypage.applied.ReceivedContentActivity

class ApplyListActivity : BaseActivity<ActivityApplyListBinding>() {
    private val viewModel by viewModels<MypageViewModel>()
    override val layoutResourceId: Int = R.layout.activity_apply_list
    private lateinit var requestType: String
    private lateinit var applyListAdapter: ApplyListAdapter

    override fun initView() {
        val intent = intent
        // apply or receive
        requestType = intent.getStringExtra("requestType")!!

        when (requestType) {
            "APPLY" -> binding.requestTypeTitle.text = APPLY_TITLE
            "RECEIVE" -> binding.requestTypeTitle.text = RECEIVE_TITLE
        }

        viewModel.getApplyList(requestType)

        applyListAdapter = ApplyListAdapter() {
            // onClickListener
            when (requestType) {
                "APPLY" -> {
                    // 선택한 신청서 내용 ViewModel에 저장
                    viewModel.selectApplyContent(it)

                    // show ApplyContentDialog
                    val applyContentDialogFragment = ApplyContentDialogFragment()
                    applyContentDialogFragment.show(supportFragmentManager, applyContentDialogFragment.tag)
                }
                "RECEIVE" -> {
                    startActivity(Intent(this, ReceivedContentActivity::class.java).apply {
                    })
                }
            }
        }

        binding.applyListRv.apply {
            adapter = applyListAdapter
            layoutManager = LinearLayoutManager(this@ApplyListActivity, LinearLayoutManager.VERTICAL, false)
        }

        viewModel.applyList.observe(this) {
            if (it.isNullOrEmpty()) {
                binding.emptyApplyList.visibility = View.VISIBLE
            } else {
                binding.emptyApplyList.visibility = View.GONE
                applyListAdapter.setList(it)
            }
        }
    }

    companion object {
        private const val APPLY_TITLE = "멘토링 신청 내역"
        private const val RECEIVE_TITLE = "멘토링 신청 받은 내역"
    }
}