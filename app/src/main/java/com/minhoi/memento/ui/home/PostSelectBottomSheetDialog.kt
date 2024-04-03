package com.minhoi.memento.ui.home

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.minhoi.memento.R
import com.minhoi.memento.databinding.BottomSheetFragmentPostSelectBinding
import com.minhoi.memento.ui.board.MentorPostActivity
import com.minhoi.memento.utils.setOnSingleClickListener

class PostSelectBottomSheetDialog : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetFragmentPostSelectBinding
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_fragment_post_select, container, false)
        return inflater.inflate(R.layout.bottom_sheet_fragment_post_select, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.mentorPostButton.setOnSingleClickListener {
            startActivity(Intent(requireContext(), MentorPostActivity::class.java))
        }
        super.onViewCreated(view, savedInstanceState)
    }

}