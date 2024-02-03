package com.minhoi.memento.ui.join

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.minhoi.memento.R
import com.minhoi.memento.databinding.FragmentSchoolInfoBinding
import kotlinx.coroutines.launch


class SchoolInfoFragment : Fragment(), YearPickerDialogFragment.YearPickerDialogListener {
    private val TAG = SchoolInfoFragment::class.simpleName

    private lateinit var binding: FragmentSchoolInfoBinding
    private val joinViewModel: JoinViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val fragmentBinding = FragmentSchoolInfoBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        binding.viewmodel = joinViewModel
        binding.lifecycleOwner = this
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpSchoolNames()
        observeSchoolInput()
        observeYearInput()

        binding.infoNextBtn.setOnClickListener {
            // 입력한 학교 이름이 Schools에 있는지 검사 해야함

            if (isSchoolBlank()) {
                binding.apply {
                    inputSchoolName.setBackgroundResource(R.drawable.round_corner_red_color)
                    schoolHint.text = "학교 이름은 필수 입력사항입니다."
                }
            }

            if (!isValidSchoolName()) {
                binding.apply {
                    inputSchoolName.setBackgroundResource(R.drawable.round_corner_red_color)
                    schoolHint.text = "존재하지 않는 학교 이름입니다."
                }
            }

            if (isYearBlank()) {
                binding.apply {
                    inputYear.setBackgroundResource(R.drawable.round_corner_red_color)
                    yearHint.text = "학번은 필수 선택사항입니다."
                }
            }

            if (!isSchoolBlank() && !isYearBlank() && isValidSchoolName()) {
                lifecycleScope.launch {
                    clickNextBtn()
                }
            }
        }

        // 학번 선택 클릭시 NumberPickerDialog 호출
        binding.inputYear.setOnClickListener {
            showYearPickerDialog()
        }
    }

    private fun setUpSchoolNames() {
        joinViewModel.schools.observe(viewLifecycleOwner) { schools ->
            binding.inputSchoolName.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    schools.map { it.name })
            )
        }
    }

    private fun clickNextBtn() {
        val schoolName = binding.inputSchoolName.text.toString()
        joinViewModel.getMajorsBySchool(schoolName)
        findNavController().navigate(R.id.action_schoolInfoFragment_to_schoolMajorFragment)
    }

    private fun showYearPickerDialog() {
        val yearPickerDialog = YearPickerDialogFragment()
        yearPickerDialog.setYearPickerDialogListener(this)
        yearPickerDialog.show(parentFragmentManager, "YearPickerDialog")
    }

    // YearPickerDialogListener에서 구현한 콜백 메서드
    override fun onYearSelected(year: Int) {
        binding.inputYear.text = year.toString()
        Log.d("AFragment", "Selected Year: $year")
    }

    private fun observeSchoolInput() {
        joinViewModel.school.observe(viewLifecycleOwner) { input ->
            if (!input.isNullOrBlank()) {
                binding.inputSchoolName.setBackgroundResource(R.drawable.round_corner_purple_color)
                binding.schoolHint.text = ""
            }
        }
    }

    private fun observeYearInput() {
        joinViewModel.year.observe(viewLifecycleOwner) { input ->
            if (!input.isNullOrBlank()) {
                binding.inputYear.setBackgroundResource(R.drawable.round_corner_purple_color)
                binding.yearHint.text = ""
            }
        }
    }

    private fun isValidSchoolName(): Boolean {
        val schools = joinViewModel.schools.value!!.map { it.name }
        return schools.contains(joinViewModel.school.value.toString())
    }

    private fun isSchoolBlank() = joinViewModel.school.value.isNullOrBlank()
    private fun isYearBlank() = joinViewModel.year.value.isNullOrBlank()

}