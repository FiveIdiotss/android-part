package com.minhoi.memento.ui.join

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.minhoi.memento.R
import com.minhoi.memento.databinding.FragmentSchoolMajorBinding
import com.minhoi.memento.ui.adapter.MajorAdapter
import com.minhoi.memento.utils.MajorItemDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SchoolMajorFragment : Fragment() {

    private lateinit var binding: FragmentSchoolMajorBinding
    private val joinViewModel: JoinViewModel by activityViewModels()
    private lateinit var majorAdapter: MajorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentSchoolMajorBinding.inflate(inflater, container, false)
        binding = fragmentBinding

        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        majorAdapter = MajorAdapter(requireContext()) {
            joinViewModel.setMajorId(it.majorId)
        }

        binding.majorRv.apply {
            adapter = majorAdapter
            layoutManager = LinearLayoutManager(requireActivity())
            addItemDecoration(MajorItemDecoration(80))
        }
        setUpMajors()

        binding.majorNextBtn.setOnClickListener {
            if (isMajorSelected()) {
                Toast.makeText(requireContext(), "학과는 필수 선택사항입니다.", Toast.LENGTH_LONG).show()
            } else {
                findNavController().navigate(R.id.action_schoolMajorFragment_to_emailInputFragment)
            }
        }
    }

    private fun setUpMajors() {
        joinViewModel.majors.observe(viewLifecycleOwner) { majors ->
            majorAdapter.setItems(majors)
        }
    }

    private fun isMajorSelected() = joinViewModel.majorId.value?.toString().isNullOrBlank()

}