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
import com.minhoi.memento.databinding.FragmentEmailVerifyCodeInputBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class EmailVerifyCodeInputFragment : Fragment() {

    private lateinit var binding: FragmentEmailVerifyCodeInputBinding
    private val joinViewModel: JoinViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val fragmentBinding =
            FragmentEmailVerifyCodeInputBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        binding.viewmodel = joinViewModel
        binding.lifecycleOwner = this
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeIsValidCode()
        observeVerificationState()

    }

    private fun observeVerificationState() {
        joinViewModel.verificationState.observe(viewLifecycleOwner) {
            if(it) {
                findNavController().navigate(R.id.action_emailVerifyCodeInputFragment_to_profileFragment)
            }
        }
    }

    private fun observeIsValidCode() {
        joinViewModel.verificationCode.observe(viewLifecycleOwner) { code ->
            if (code.length >= 4) {
                lifecycleScope.launch {
                    joinViewModel.verifyCode()

                    withContext(Dispatchers.Main) {
                        binding.codeValidationHint.text = "유효하지 않은 코드입니다"
                    }
                }
            }
        }
    }

}