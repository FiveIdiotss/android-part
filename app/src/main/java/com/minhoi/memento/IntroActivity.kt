package com.minhoi.memento

import android.content.Intent
import android.os.Bundle
import com.minhoi.memento.base.BaseActivity
import com.minhoi.memento.databinding.ActivityIntroBinding
import com.minhoi.memento.ui.join.JoinActivity
import com.minhoi.memento.ui.login.LoginActivity

class IntroActivity : BaseActivity<ActivityIntroBinding>() {

    override val layoutResourceId: Int = R.layout.activity_intro

    override fun initView() {
        binding.apply {
            toJoinBtn.setOnClickListener {
                startActivity(Intent(this@IntroActivity, JoinActivity::class.java))
            }
            toLoginBtn.setOnClickListener {
                startActivity(Intent(this@IntroActivity, LoginActivity::class.java))
            }
        }
    }
}