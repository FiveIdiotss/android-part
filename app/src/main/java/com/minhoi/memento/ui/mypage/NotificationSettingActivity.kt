package com.minhoi.memento.ui.mypage

import com.minhoi.memento.MentoApplication
import com.minhoi.memento.R
import com.minhoi.memento.base.BaseActivity
import com.minhoi.memento.databinding.ActivityNotificationSettingBinding

class NotificationSettingActivity : BaseActivity<ActivityNotificationSettingBinding>() {
    override val layoutResourceId: Int = R.layout.activity_notification_setting

    override fun initView() {
        setupToolbar("알림 설정")

        binding.apply {
            chatNotificationSwitch.isChecked =
                MentoApplication.notificationPermissionPrefs.getChatPermission()
            matchingNotificationSwitch.isChecked =
                MentoApplication.notificationPermissionPrefs.getMatchPermission()
            replyNotificationSwitch.isChecked =
                MentoApplication.notificationPermissionPrefs.getReplyPermission()
            applyNotificationSwitch.isChecked =
                MentoApplication.notificationPermissionPrefs.getApplyPermission()
        }

        binding.apply {
            chatNotificationSwitch.setOnCheckedChangeListener { _, isChecked ->
                MentoApplication.notificationPermissionPrefs.setChatPermission(isChecked)
            }

            matchingNotificationSwitch.setOnCheckedChangeListener { _, isChecked ->
                MentoApplication.notificationPermissionPrefs.setMatchPermission(isChecked)
            }

            replyNotificationSwitch.setOnCheckedChangeListener { _, isChecked ->
                MentoApplication.notificationPermissionPrefs.setReplyPermission(isChecked)
            }

            applyNotificationSwitch.setOnCheckedChangeListener { _, isChecked ->
                MentoApplication.notificationPermissionPrefs.setApplyPermission(isChecked)
            }
        }
    }
}