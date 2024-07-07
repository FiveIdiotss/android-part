package com.minhoi.memento.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.minhoi.memento.R
import com.minhoi.memento.databinding.FragmentSettingBottomSheetDialogBinding

class SettingBottomSheetDialog : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentSettingBottomSheetDialogBinding

    interface OnSettingValueClickListener {
        fun onSettingValueSelected(value: SettingOption)
    }

    var listener: OnSettingValueClickListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting_bottom_sheet_dialog, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categories = listOf(SettingOption.EDIT_POST, SettingOption.DELETE_POST)

        val arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, categories)
        binding.settingLv.apply {
            adapter = arrayAdapter
            setOnItemClickListener { _, _, position, _ ->
                listener?.onSettingValueSelected(categories[position])
                dismiss()
            }
        }
    }

    fun selectSetting(listener: OnSettingValueClickListener) {
        this.listener = listener
    }

}