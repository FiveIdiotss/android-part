package com.minhoi.memento.ui.mypage

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.minhoi.memento.R
import com.minhoi.memento.base.BaseFragment
import com.minhoi.memento.databinding.FragmentMypageBinding
import com.minhoi.memento.utils.setOnSingleClickListener

class MypageFragment : BaseFragment<FragmentMypageBinding>() {

    override val layoutResourceId: Int = R.layout.fragment_mypage

    override fun initView() {

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
        }
    }

}