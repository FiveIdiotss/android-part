package com.minhoi.memento.ui.join

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.minhoi.memento.R
import com.minhoi.memento.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Pattern

private const val MISMATCH_PASSWORD_TEXT = "비밀번호가 일치하지 않습니다"
private const val INVALID_PASSWORD_FORMAT_TEXT = "비밀번호는 영어, 숫자, 특수문자를 포함하여 8~16자 입니다"
private const val VALID_INPUT_TEXT = ""
private const val PASSWORD_FORMAT =
    "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&.])[A-Za-z[0-9]$@$!%*#?&.]{8,16}$"

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private var passwordCheckFlag = false

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

        binding.joinBtn.setOnClickListener {
            if (canPerformJoin()) {
                joinViewModel.join()
            }
        }
        observeIsRegularPassword()
        observeIsPasswordEqual()
    }


    private fun observeIsRegularPassword() {

        joinViewModel.password.observe(viewLifecycleOwner) { input ->
            val pattern = Pattern.matches(PASSWORD_FORMAT, input) // 패턴이 맞는지 확인
            when (pattern) {
                true -> {
                    binding.apply {
                        inputPassword.setBackgroundResource(R.drawable.round_corner_purple_color)
                        passwordText.text = VALID_INPUT_TEXT
                    }
                    passwordCheckFlag = true
                }

                else -> {
                    binding.apply {
                        inputPassword.setBackgroundResource(R.drawable.round_corner_red_color)
                        passwordText.text = INVALID_PASSWORD_FORMAT_TEXT
                    }
                    passwordCheckFlag = false
                }
            }
        }
    }

    private fun observeIsPasswordEqual() {
        joinViewModel.passwordCheck.observe(viewLifecycleOwner) { input ->
            val password = joinViewModel.password.value.toString()
            val isPasswordEqual = input.equals(password)

            if (isPasswordEqual) {
                binding.apply {
                    inputPasswordCheck.setBackgroundResource(R.drawable.round_corner_purple_color)
                    passwordCheckText.text = VALID_INPUT_TEXT
                }
            } else {
                binding.apply {
                    inputPasswordCheck.setBackgroundResource(R.drawable.round_corner_red_color)
                    passwordCheckText.text = MISMATCH_PASSWORD_TEXT
                }
            }
        }
    }

    private fun isNameBlank() = joinViewModel.name.value.isNullOrBlank()
    private fun isPasswordBlank() = joinViewModel.password.value.isNullOrBlank()
    private fun isPasswordCheckBlank() = joinViewModel.passwordCheck.value.isNullOrBlank()

    private fun canPerformJoin(): Boolean {
        return passwordCheckFlag &&
                !isNameBlank() &&
                !isPasswordBlank() &&
                !isPasswordCheckBlank()
    }

}