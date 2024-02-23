package com.minhoi.memento.ui.mypage

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.minhoi.memento.databinding.ApplyContentDialogBinding
import com.minhoi.memento.ui.board.BoardActivity
import com.minhoi.memento.utils.dialogFragmentResize
import com.minhoi.memento.utils.setOnSingleClickListener

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

        binding.goToBoardContentBtn.setOnSingleClickListener {
            goToBoard()
            dismiss()
        }
    }

    private fun observeApplyContent() {
        viewModel.applyContent.observe(viewLifecycleOwner) {
            binding.applyContent.text = it.content
        }
    }

    private fun goToBoard() {
        startActivity(Intent(requireContext(), BoardActivity::class.java).apply {
            putExtra("boardId", viewModel.applyContent.value?.boardId)
        })
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