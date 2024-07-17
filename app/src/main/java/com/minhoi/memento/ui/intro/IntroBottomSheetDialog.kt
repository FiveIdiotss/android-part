package com.minhoi.memento.ui.intro

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.minhoi.memento.R
import com.minhoi.memento.databinding.FragmentIntroBottomSheetDialogBinding
import com.minhoi.memento.ui.join.JoinActivity
import com.minhoi.memento.ui.login.LoginActivity
import com.minhoi.memento.utils.setOnSingleClickListener

class IntroBottomSheetDialog : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentIntroBottomSheetDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_intro_bottom_sheet_dialog,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            emailJoinBtn.setOnSingleClickListener {
                startActivity(Intent(requireContext(), JoinActivity::class.java))
                dismiss()
            }

            emailLoginBtn.setOnSingleClickListener {
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                dismiss()
            }
        }
    }

}