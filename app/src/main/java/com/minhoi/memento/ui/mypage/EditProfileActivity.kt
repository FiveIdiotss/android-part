package com.minhoi.memento.ui.mypage

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.minhoi.memento.R
import com.minhoi.memento.base.BaseActivity
import com.minhoi.memento.databinding.ActivityEditProfileBinding
import com.minhoi.memento.ui.UiState
import com.minhoi.memento.utils.hideLoading
import com.minhoi.memento.utils.setOnSingleClickListener
import com.minhoi.memento.utils.showLoading
import com.minhoi.memento.utils.showToast
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class EditProfileActivity : BaseActivity<ActivityEditProfileBinding>(),
    EditProfileImageBottomSheetDialog.PhotoPickerListener {

    override val layoutResourceId: Int = R.layout.activity_edit_profile
    private val viewModel by viewModels<MypageViewModel>()
    private var imageUri: Uri? = null

    override fun initView() {
        observeMemberInfo()

        binding.editImageBtn.setOnSingleClickListener {
            EditProfileImageBottomSheetDialog().show(supportFragmentManager, "editProfileImage")
        }

        /*
        * 이미지 업로드 상태 Flow를 수집하여 UI를 업데이트 (기본 프로필로 수정시 imageUrl이 null)
         */
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.imageUploadState.collectLatest { state ->
                    when (state) {
                        is UiState.Empty -> {}
                        is UiState.Loading -> {
                            supportFragmentManager.showLoading()
                        }

                        is UiState.Success -> {
                            supportFragmentManager.hideLoading()
                            when (imageUri) {
                                null -> {
                                    binding.editImageBtn.setImageDrawable(
                                        ResourcesCompat.getDrawable(
                                            resources,
                                            R.drawable.defaultimage,
                                            null
                                        )
                                    )
                                }
                                // 이미지 업로드 후 imageUri null로 변경 (다시 기본 이미지로 수정할 가능성)
                                else -> {
                                    binding.editImageBtn.setImageURI(imageUri)
                                    imageUri = null
                                }
                            }
                        }

                        is UiState.Error -> {
                            supportFragmentManager.hideLoading()
                            showToast("이미지 업로드 중 네트워크 오류가 발생하였습니다.")
                        }
                    }
                }
            }
        }
    }

    private fun observeMemberInfo() {
        viewModel.memberInfo.observe(this) {
            binding.member = it
        }
    }
    /*
    * 이미지 선택 후 BottomSheetDialogFragment를 닫고 선택한 이미지를 Activity로 전달받는 인터페이스
     */
    override fun onPhotoPicked(uri: Uri?) {
        if (uri != null) {
            imageUri = uri
            uploadImage(uri)
        }
    }

    override fun onSetDefaultProfile() {
        viewModel.setDefaultProfileImage()
    }

    fun uploadImage(uri: Uri) {
        try {
            val image = convertUriToPart(uri)
            viewModel.uploadProfileImage(image)
        } catch (e: Exception) {
            showToast("이미지를 불러오는데 실패했습니다.")
        }
    }

    @SuppressLint("Recycle")
    fun convertUriToPart(uri: Uri): MultipartBody.Part {
        val inputStream = contentResolver.openInputStream(uri)
        var body: MultipartBody.Part? = null
        inputStream?.let {
            val requestBody = it.readBytes().toRequestBody("image/*".toMediaTypeOrNull())
            body = MultipartBody.Part.createFormData("imageFile", "profile", requestBody)
        }
        return body ?: throw Exception("Image is null")
    }
}