package com.minhoi.memento.ui.mypage

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.minhoi.memento.databinding.FragmentEditProfileImageBottomSheetDialogBinding
import com.minhoi.memento.utils.setOnSingleClickListener

class EditProfileImageBottomSheetDialog : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentEditProfileImageBottomSheetDialogBinding
    private val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            FragmentEditProfileImageBottomSheetDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            selectPicture.setOnSingleClickListener {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        permissions,
                        PERMISSION_CODE
                    )
                } else {
                    pickImageFromGallery()
                }
            }
            setDefaultProfile.setOnSingleClickListener {
                (activity as? PhotoPickerListener)?.onSetDefaultProfile()
                dismiss()
            }
            cancel.setOnSingleClickListener {
                dismiss()
            }
        }
    }

    private val pickImageContract =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            (activity as? PhotoPickerListener)?.onPhotoPicked(uri)
            dismiss()
        }

    private fun pickImageFromGallery() {
        pickImageContract.launch("image/*")
    }

    // 선택한 이미지의 URI를 Activity로 전달하기 위해 인터페이스 사용
    interface PhotoPickerListener {
        fun onPhotoPicked(uri: Uri?)
        fun onSetDefaultProfile()
    }

    companion object {
        private const val PERMISSION_CODE = 1001
    }
}
