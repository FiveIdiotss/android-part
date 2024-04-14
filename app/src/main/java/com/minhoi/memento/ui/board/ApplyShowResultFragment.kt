package com.minhoi.memento.ui.board

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.minhoi.memento.R
import com.minhoi.memento.base.BaseFragment
import com.minhoi.memento.databinding.FragmentApplyShowResultBinding

class ApplyShowResultFragment : BaseFragment<FragmentApplyShowResultBinding>() {
    override val layoutResourceId: Int = R.layout.fragment_apply_show_result
    private val viewModel = ViewModelProvider(requireActivity())[BoardViewModel::class.java]

    override fun initView() {

    }
}