package com.minhoi.memento.ui.chat

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.minhoi.memento.R
import com.minhoi.memento.databinding.FragmentSelectFileDialogBinding
import com.minhoi.memento.utils.FileManager
import com.minhoi.memento.utils.dialogFragmentResize
import com.minhoi.memento.utils.setOnSingleClickListener
import com.minhoi.memento.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SelectFileDialog @Inject constructor(
    private val fileManager: FileManager,
) : DialogFragment() {

    private lateinit var binding: FragmentSelectFileDialogBinding
    private lateinit var pickImageContract: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var pickDocumentContract: ActivityResultLauncher<Array<String>>
    private val viewModel: ChatViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_select_file_dialog, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pickImageContract =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
                uri?.let {
                    val mimeType = fileManager.getFileMimeType(uri)
                    val file = fileManager.uriToMultipartBodyPart(uri, mimeType!!, "file")
                    viewModel.sendFile(file!!)
                }
            }

        pickDocumentContract =
            registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
                uri?.let {
                    val mimeType = fileManager.getFileMimeType(uri)
                    val fileName = "file"
                    when {
                        mimeType == null -> {
                            // MIME 타입을 확인할 수 없는 경우
                            requireContext().showToast("알 수 없는 파일 형식입니다.")
                            return@registerForActivityResult
                        }

                        mimeType.startsWith("application/pdf")
                                || mimeType.startsWith("image/")
                                || mimeType.startsWith("application/zip") -> {
                            // PDF 파일 처리
                            val file = fileManager.uriToMultipartBodyPart(uri, mimeType, fileName)
                            viewModel.sendFile(file!!)
                        }

                        else -> {
                            // 기타 파일 형식 처리
                            requireContext().showToast("지원하지 않는 파일 형식입니다.")
                            return@registerForActivityResult
                        }
                    }
                }
            }

        binding.selectFileBtn.setOnSingleClickListener {
            pickDocumentFromFiles()
        }

        binding.selectImageBtn.setOnSingleClickListener {
            pickImageFromGallery()
        }
    }

    override fun onResume() {
        super.onResume()
        context?.dialogFragmentResize(this@SelectFileDialog, 0.8f, 0.25f)
    }


    private fun pickDocumentFromFiles() {
        // 선택할 파일 유형을 정의하는 MIME 타입 배열
        val mimeTypes = arrayOf(
            "application/pdf",
            "application/zip",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        )
        pickDocumentContract.launch(mimeTypes)
    }

    private fun pickImageFromGallery() {
        pickImageContract.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

}