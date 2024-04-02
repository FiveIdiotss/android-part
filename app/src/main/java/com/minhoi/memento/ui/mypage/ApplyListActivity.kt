package com.minhoi.memento.ui.mypage

import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.minhoi.memento.R
import com.minhoi.memento.adapter.ApplyListAdapter
import com.minhoi.memento.adapter.ReceivedListAdapter
import com.minhoi.memento.base.BaseActivity
import com.minhoi.memento.databinding.ActivityApplyListBinding
import com.minhoi.memento.ui.board.BoardActivity
import com.minhoi.memento.ui.mypage.received.ReceivedContentActivity

class ApplyListActivity : BaseActivity<ActivityApplyListBinding>() {
    private val viewModel by viewModels<MypageViewModel>()
    override val layoutResourceId: Int = R.layout.activity_apply_list
    private lateinit var requestType: String
    private lateinit var applyListAdapter: ApplyListAdapter
    private lateinit var receivedListAdapter: ReceivedListAdapter

    override fun initView() {
        val intent = intent
        // apply or receive
        requestType = intent.getStringExtra("requestType")!!

        when (requestType) {
            TYPE_APPLY -> {
                viewModel.getApplyList()
                binding.requestTypeTitle.text = APPLY_TITLE
                applyListAdapter = ApplyListAdapter(
                    onBoardClickListener = {
                        // onClickListener
                        // 선택한 신청서 내용 Activity에 전달
                        startActivity(Intent(this, BoardActivity::class.java).apply {
                            putExtra("applyDto", it)
                        })
                    },
                    onShowApplyContentListener = {
                        // 지원서 보기 선택시 선택한 신청서 정보 가져오기
                        viewModel.getApplyInfo(it.applyId)

                        val applyContentDialogFragment = ApplyContentDialogFragment()
                        applyContentDialogFragment.show(supportFragmentManager, applyContentDialogFragment.tag)
                    }
                )
                observeApplyList()
            }

            TYPE_RECEIVE -> {
                viewModel.getReceivedList()
                binding.requestTypeTitle.text = RECEIVE_TITLE
                receivedListAdapter = ReceivedListAdapter() {
                    // onClickListener
                    // 선택한 신청서 내용 Activity에 전달
                    startActivity(Intent(this, ReceivedContentActivity::class.java).apply {
                        putExtra("receivedDto", it)
                    })
                }
                observeReceivedList()
            }
        }


        binding.applyListRv.apply {
            when(requestType) {
                TYPE_APPLY -> adapter = applyListAdapter
                TYPE_RECEIVE -> adapter = receivedListAdapter
            }
            layoutManager = LinearLayoutManager(this@ApplyListActivity, LinearLayoutManager.VERTICAL, false)
        }

    }

    private fun observeApplyList() {
        viewModel.applyList.observe(this) {
            if (it.isNullOrEmpty()) {
                binding.emptyApplyList.visibility = View.VISIBLE
            } else {
                binding.emptyApplyList.visibility = View.GONE
                applyListAdapter.setList(it)
            }
        }
    }

    private fun observeReceivedList() {
        viewModel.receivedList.observe(this) {
            if (it.isNullOrEmpty()) {
                binding.emptyApplyList.visibility = View.VISIBLE
            } else {
                binding.emptyApplyList.visibility = View.GONE
                receivedListAdapter.setList(it)
            }
        }
    }

    companion object {
        private const val TYPE_APPLY = "APPLY"
        private const val TYPE_RECEIVE = "RECEIVE"
        private const val APPLY_TITLE = "멘토링 신청 내역"
        private const val RECEIVE_TITLE = "멘토링 신청 받은 내역"
    }
}