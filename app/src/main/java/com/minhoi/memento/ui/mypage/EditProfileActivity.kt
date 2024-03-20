package com.minhoi.memento.ui.mypage

import androidx.activity.viewModels
import com.minhoi.memento.R
import com.minhoi.memento.base.BaseActivity
import com.minhoi.memento.databinding.ActivityEditProfileBinding

class EditProfileActivity : BaseActivity<ActivityEditProfileBinding>() {

    override val layoutResourceId: Int = R.layout.activity_edit_profile
    private val viewModel by viewModels<MypageViewModel>()

    override fun initView() {
        binding.apply {
            member = viewModel.getMemberInfo()
            lifecycleOwner = this@EditProfileActivity
        }
    }
}