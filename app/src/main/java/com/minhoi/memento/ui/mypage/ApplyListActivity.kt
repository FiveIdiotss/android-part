package com.minhoi.memento.ui.mypage

import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.minhoi.memento.R
import com.minhoi.memento.adapter.ApplyListAdapter
import com.minhoi.memento.base.BaseActivity
import com.minhoi.memento.databinding.ActivityApplyListBinding

class ApplyListActivity : BaseActivity<ActivityApplyListBinding>() {
    private val viewModel by viewModels<MypageViewModel>()
    override val layoutResourceId: Int = R.layout.activity_apply_list
    private lateinit var applyListAdapter: ApplyListAdapter

    override fun initView() {
        viewModel.getApplyList()

        applyListAdapter = ApplyListAdapter() {
            // onClickListener
            // 선택한 신청서 내용 ViewModel에 저장
            viewModel.selectApplyContent(it)

            // show ApplyContentDialog
            val applyContentDialogFragment = ApplyContentDialogFragment()
            applyContentDialogFragment.show(supportFragmentManager, applyContentDialogFragment.tag)
        }

        binding.applyListRv.apply {
            adapter = applyListAdapter
            layoutManager = LinearLayoutManager(this@ApplyListActivity, LinearLayoutManager.VERTICAL, false)
        }

        viewModel.applyList.observe(this) {
            applyListAdapter.setList(it)
        }
    }
}