package com.minhoi.memento.ui.mypage

import android.view.MenuItem
import com.minhoi.memento.MentoApplication
import com.minhoi.memento.R
import com.minhoi.memento.base.BaseActivity
import com.minhoi.memento.databinding.ActivityNotificationSettingBinding

class NotificationSettingActivity : BaseActivity<ActivityNotificationSettingBinding>() {
    override val layoutResourceId: Int = R.layout.activity_notification_setting

    override fun initView() {
        setUpToolbar()

        binding.chatNotificationSwitch.isChecked =
            when (MentoApplication.notificationPermissionPrefs.getChatPermission()) {
                true -> true
                false -> false
            }

        binding.chatNotificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            MentoApplication.notificationPermissionPrefs.setChatPermission(isChecked)
        }
    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.notificationSettingToolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}