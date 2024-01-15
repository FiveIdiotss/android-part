package com.minhoi.memento.ui.join

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.minhoi.memento.R
import com.minhoi.memento.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val fragmentBinding = FragmentProfileBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.inputEmail.addTextChangedListener(emailInputFormal)
    }

    private val emailInputFormal = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches()) {
                binding.emailCheckText.text = "이메일 형식으로 입력해주세요."
                binding.inputEmail.setBackgroundResource(R.drawable.round_corner_red_color)  // 적색 테두리
            } else {
                binding.emailCheckText.text = ("")        //에러 메세지 제거
                binding.inputEmail.setBackgroundResource(R.drawable.round_corner_purple_color)  //보라색 테두리
            }
        }
    }

}