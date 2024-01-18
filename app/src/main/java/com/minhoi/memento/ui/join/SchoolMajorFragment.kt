package com.minhoi.memento.ui.join

import android.icu.lang.UCharacter.VerticalOrientation
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.minhoi.memento.MajorAdapter
import com.minhoi.memento.MajorItemDecoration
import com.minhoi.memento.databinding.FragmentSchoolMajorBinding

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
        setObserve()
    }

    private fun setObserve() {
        joinViewModel.majors.observe(viewLifecycleOwner) { majors ->
            majorAdapter.setItems(majors)
        }
    }


}