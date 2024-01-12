package com.minhoi.memento.ui.join

import android.os.Bundle
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


class SchoolInfoFragment : Fragment() {

    private lateinit var binding: FragmentSchoolInfoBinding
    private val joinViewModel: JoinViewModel by activityViewModels()
    private lateinit var adapter: ArrayAdapter<String>
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
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserve()

        binding.infoNextBtn.setOnClickListener {
            // 유효성 검사 진행 해야함
            lifecycleScope.launch {
                clickNextBtn()
            }
        }
    }

    private fun setObserve() {
        joinViewModel.schools.observe(viewLifecycleOwner) { schools ->
            binding.inputSchoolName.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, schools.map { it.name }))
        }
    }

    private suspend fun clickNextBtn() {
        val schoolName = binding.inputSchoolName.text.toString()
        joinViewModel.getMajorsBySchool(schoolName)
        findNavController().navigate(R.id.action_schoolInfoFragment_to_schoolMajorFragment)
    }

}