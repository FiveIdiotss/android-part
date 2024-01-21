package com.minhoi.memento.ui.join

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.minhoi.memento.R
import com.minhoi.memento.databinding.FragmentProfileBinding
import kotlinx.coroutines.launch
import java.util.regex.Pattern

private const val INVALID_EMAIL_FORMAT_TEXT = "이메일 형식이 아닙니다"
private const val MISMATCH_PASSWORD_TEXT = "비밀번호가 일치하지 않습니다"
private const val INVALID_PASSWORD_FORMAT_TEXT = "비밀번호는 영어, 숫자, 특수문자를 포함하여 8~16자 입니다"
private const val VALID_INPUT_TEXT = ""
private const val PASSWORD_FORMAT = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&.])[A-Za-z[0-9]$@$!%*#?&.]{8,16}$"
class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private var regularEmailFlag = false

    private val joinViewModel: JoinViewModel by activityViewModels()
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
        binding.viewmodel = joinViewModel
        binding.lifecycleOwner = this
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.inputEmail.addTextChangedListener(emailInputFormal)
    }

    private val emailInputFormal = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    private fun observeIsRegularEmail() {
        joinViewModel.email.observe(viewLifecycleOwner) { input ->
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
                binding.apply {
                    emailCheckText.text = INVALID_EMAIL_FORMAT_TEXT
                    inputEmail.setBackgroundResource(R.drawable.round_corner_red_color)
                }
                regularEmailFlag = false
            } else {
                binding.apply {
                    binding.emailCheckText.text = VALID_INPUT_TEXT      //에러 메세지 제거
                    binding.inputEmail.setBackgroundResource(R.drawable.round_corner_purple_color)
                }
                regularEmailFlag = true
            }
        }
    }
}