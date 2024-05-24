package com.minhoi.memento.ui.board

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.minhoi.memento.R
import com.minhoi.memento.databinding.FragmentSelectBoardCategoryBottomSheetDialogBinding

class SelectBoardCategoryBottomSheetDialog() : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentSelectBoardCategoryBottomSheetDialogBinding

    interface OnCategorySelectedListener {
        fun onCategorySelected(category: String)
    }

    var listener: OnCategorySelectedListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_select_board_category_bottom_sheet_dialog, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categories = listOf("모든 단과대", "이공", "자연", "인문", "사회", "의약", "예체능", "사범")

        val arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, categories)
        binding.categoryListView.apply {
            adapter = arrayAdapter
            setOnItemClickListener { _, _, position, _ ->
                listener?.onCategorySelected(categories[position])
                dismiss()
            }
        }
    }

    fun selectCategory(listener: OnCategorySelectedListener) {
        this.listener = listener
    }

}