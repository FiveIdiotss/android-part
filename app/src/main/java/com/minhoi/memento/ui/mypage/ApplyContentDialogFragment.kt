package com.minhoi.memento.ui.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.minhoi.memento.databinding.ApplyContentDialogBinding
import com.minhoi.memento.ui.UiState
import com.minhoi.memento.utils.dialogFragmentResize
import com.minhoi.memento.utils.hideLoading
import com.minhoi.memento.utils.showLoading
import com.minhoi.memento.utils.showToast
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ApplyContentDialogFragment: DialogFragment() {

    private var _binding: ApplyContentDialogBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MypageViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = ApplyContentDialogBinding.inflate(inflater, container, false)
        // Activity에서 사용하는 ViewModel 가져오기
        viewModel = ViewModelProvider(requireActivity())[MypageViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDialog()
    }

    fun initDialog() {
        observeApplyContent()
    }

    private fun observeApplyContent() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.applyContent.collectLatest {
                    when (it) {
                        is UiState.Empty -> {}
                        is UiState.Loading -> {
                            parentFragmentManager.showLoading()
                        }
                        is UiState.Success -> {
                            parentFragmentManager.hideLoading()
                            binding.applyContent = it.data
                        }
                        is UiState.Error -> {
                            parentFragmentManager.hideLoading()
                            requireContext().showToast(it.error?.message.toString())
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        context?.dialogFragmentResize(this@ApplyContentDialogFragment, 0.75f, 0.75f)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}