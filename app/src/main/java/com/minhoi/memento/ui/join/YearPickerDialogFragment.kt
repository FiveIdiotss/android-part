package com.minhoi.memento.ui.join

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.NumberPicker
import androidx.fragment.app.DialogFragment
import com.minhoi.memento.R

class YearPickerDialogFragment : DialogFragment() {

    interface YearPickerDialogListener {
        fun onYearSelected(year: Int)
    }

    private var listener: YearPickerDialogListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val layout = layoutInflater.inflate(R.layout.fragment_year_picker, null)
        val yearPicker = layout.findViewById<NumberPicker>(R.id.yearPicker)

        yearPicker.apply {
            minValue = 2011
            maxValue = 2024
            wrapSelectorWheel = false
        }

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(layout)
            .setPositiveButton("확인") { _, _ ->
                // OK 버튼이 눌렸을 때의 처리
                val selectedYear = yearPicker.value
                listener?.onYearSelected(selectedYear)
                // 선택된 연도를 사용하여 필요한 작업 수행
            }
            .setNegativeButton("취소") { _, _ ->
                // Cancel 버튼이 눌렸을 때의 처리
                dialog?.dismiss()
            }

        return builder.create()
    }

    fun setYearPickerDialogListener(listener: YearPickerDialogListener) {
        this.listener = listener
    }
}