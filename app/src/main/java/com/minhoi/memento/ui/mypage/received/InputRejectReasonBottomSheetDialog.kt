package com.minhoi.memento.ui.mypage.received

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.minhoi.memento.databinding.FragmentInputRejectReasonBottomSheetDialogBinding
import com.minhoi.memento.ui.mypage.MypageViewModel
import com.minhoi.memento.utils.setOnSingleClickListener

class InputRejectReasonBottomSheetDialog : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentInputRejectReasonBottomSheetDialogBinding
    private val viewModel by activityViewModels<MypageViewModel>()
    private var rejectReasonListener: RejectReasonListener? = null

    interface RejectReasonListener {
        fun onRejectReasonSubmit(reason: String)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentInputRejectReasonBottomSheetDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.submitBtn.setOnSingleClickListener {
//            if (binding.inputRejectReason.text.length < 10) {
//                binding.inputRejectReason.error = "10자 이상 입력해주세요"
//                return@setOnSingleClickListener
//            }
            rejectReasonListener?.onRejectReasonSubmit(binding.inputRejectReason.text.toString())
            dismiss()
        }
    }

    fun setRejectReasonListener(listener: RejectReasonListener) {
        this.rejectReasonListener = listener
    }
}