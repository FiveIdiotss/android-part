package com.minhoi.memento.ui.join

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.minhoi.memento.R
import com.minhoi.memento.databinding.FragmentEmailVerifyBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val INVALID_EMAIL_FORMAT_TEXT = "이메일 형식이 아닙니다"
private const val VALID_INPUT_TEXT = ""
@AndroidEntryPoint
class EmailInputFragment : Fragment() {

    private lateinit var binding: FragmentEmailVerifyBinding
    private val joinViewModel: JoinViewModel by activityViewModels()

    private var regularEmailState = false
    private var validEmailState = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val fragmentBinding = FragmentEmailVerifyBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        binding.viewmodel = joinViewModel
        binding.lifecycleOwner = this
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeIsRegularEmail()
        observeIsValidSchoolEmail()

        binding.verifyBtn.setOnClickListener {
            if (!isEmailBlank() && regularEmailState) {
                lifecycleScope.launch {
                    joinViewModel.verifySchoolAndEmail()
                    withContext(Dispatchers.Main) {
                        when (validEmailState) {
                            true -> {
                                findNavController().navigate(R.id.action_emailInputFragment_to_emailVerifyCodeInputFragment)
                            }
                            else -> {
                                binding.emailVerificationHint.text = "유효하지 않은 대학교 이메일입니다. 다시 입력해주세요"
                            }
                        }
                    }
                }
            }
        }
    }

    private fun observeIsRegularEmail() {
        joinViewModel.email.observe(viewLifecycleOwner) { input ->
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
                binding.apply {
                    emailVerificationHint.text = INVALID_EMAIL_FORMAT_TEXT
                    inputEmail.setBackgroundResource(R.drawable.round_corner_red_color)
                    verifyBtn.apply {
                        isEnabled = false
                        setBackgroundResource(R.drawable.round_corner_gray_filled)
                    }
                }
                regularEmailState = false
            } else {
                binding.apply {
                    binding.emailVerificationHint.text = VALID_INPUT_TEXT      //에러 메세지 제거
                    binding.inputEmail.setBackgroundResource(R.drawable.round_corner_blue_color)
                    verifyBtn.apply {
                        isEnabled = true
                        setBackgroundResource(R.drawable.round_corner_blue_filled)
                    }
                }
                regularEmailState = true
            }
        }
    }

    private fun observeIsValidSchoolEmail() {
        joinViewModel.emailAndSchoolVerificationState.observe(viewLifecycleOwner) {
            when (it) {
                true -> {
                    validEmailState = true
                }
                else -> {}
            }
        }
    }

    private fun isEmailBlank() = joinViewModel.email.value.isNullOrBlank()

}