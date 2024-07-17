package com.minhoi.memento.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.minhoi.memento.R
import com.minhoi.memento.databinding.BottomSheetFragmentPostSelectBinding
import com.minhoi.memento.ui.board.post.MentorPostActivity
import com.minhoi.memento.utils.setOnSingleClickListener

class PostSelectBottomSheetDialog : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetFragmentPostSelectBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_fragment_post_select, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mentorWriteLayout.setOnSingleClickListener {
            startActivity(Intent(requireContext(), MentorPostActivity::class.java))
            dismiss()
        }
    }

}