package com.minhoi.memento.ui.chat

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.minhoi.memento.R
import com.minhoi.memento.databinding.FragmentChatImageViewerDialogBinding

class ChatImageViewerDialog : DialogFragment() {

    private lateinit var binding: FragmentChatImageViewerDialogBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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