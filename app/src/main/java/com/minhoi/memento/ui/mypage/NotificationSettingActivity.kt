package com.minhoi.memento.ui.mypage

import com.minhoi.memento.MentoApplication
import com.minhoi.memento.R
import com.minhoi.memento.base.BaseActivity
import com.minhoi.memento.databinding.ActivityNotificationSettingBinding

class NotificationSettingActivity : BaseActivity<ActivityNotificationSettingBinding>() {
    override val layoutResourceId: Int = R.layout.activity_notification_setting

    override fun initView() {
        setupToolbar("알림 설정")

        binding.chatNotificationSwitch.isChecked =
            when (MentoApplication.notificationPermissionPrefs.getChatPermission()) {
                true -> true
                false -> false
            }

        binding.chatNotificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            MentoApplication.notificationPermissionPrefs.setChatPermission(isChecked)
        }
    }
}