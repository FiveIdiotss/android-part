package com.minhoi.memento.ui.chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.minhoi.memento.databinding.FragmentChatImageViewerDialogBinding
import com.minhoi.memento.ui.UiState
import com.minhoi.memento.utils.hideLoading
import com.minhoi.memento.utils.setOnSingleClickListener
import com.minhoi.memento.utils.showLoading
import com.minhoi.memento.utils.showToast
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class ChatImageViewerDialog : DialogFragment() {

    private lateinit var binding: FragmentChatImageViewerDialogBinding
    private val viewModel: ChatViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatImageViewerDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val message = arguments?.getString("key")
        Log.d("ChatImageDialog", "onViewCreated: $message")
        Glide.with(requireContext())
            .load(message)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .apply(RequestOptions().fitCenter())
            .into(binding.chatImageView)

        binding.apply {
            closeBtn.setOnSingleClickListener {
                dismiss()
            }
            downloadBtn.setOnSingleClickListener {
                // image download
                viewModel.saveImageToGallery(message!!)
            }
        }
        observeSaveResult()
    }

    private fun observeSaveResult() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.saveImageState.collectLatest { state ->
                    when (state) {
                        is UiState.Empty -> {}
                        is UiState.Loading -> {
                            parentFragmentManager.showLoading()
                            requireContext().showToast("파일 저장을 사직합니다")

                        }
                        is UiState.Success -> {
                            parentFragmentManager.hideLoading()
                            requireContext().showToast("파일이 저장되었습니다")
                        }
                        is UiState.Error -> {
                            parentFragmentManager.hideLoading()
                            requireContext().showToast("일시적인 오류가 발생하였습니다. 다시 시도해주세요")
                        }
                    }
                }
            }
        }
    }

    companion object {
        fun newInstance(message: String): ChatImageViewerDialog {
            val frag = ChatImageViewerDialog()
            val args = Bundle()
            args.putString("key", message) // "key"는 문자열을 식별하기 위한 임의의 문자열입니다.
            frag.arguments = args
            return frag
        }
    }

}