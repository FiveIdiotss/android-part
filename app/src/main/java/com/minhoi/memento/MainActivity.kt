package com.minhoi.memento

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.firebase.messaging.FirebaseMessaging
import com.minhoi.memento.base.BaseActivity
import com.minhoi.memento.databinding.ActivityMainBinding
import com.minhoi.memento.ui.home.HomeViewModel
import com.minhoi.memento.ui.home.PostSelectBottomSheetDialog

class MainActivity : BaseActivity<ActivityMainBinding>() {
    override val layoutResourceId: Int = R.layout.activity_main
    private val viewModel by viewModels<HomeViewModel>()

    override fun initView() {
        setBottomNavigation()
        askNotificationPermission()
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task: Task<String> ->
                if (!task.isSuccessful) {
                    Log.d("MainActivity", "initView: success")
                    return@addOnCompleteListener
                }
                val token = task.result
                viewModel.saveFCMToken(token)
                Log.d("FCMLog", "Current token: $token")
            }
    }

    private fun setBottomNavigation() {
        /*
        NavController는 onNavDestinationSelected() 호출.
        따라서 MenuItem의 id가 대상의 id와 일치하면 Navcontroller는 그 대상으로 이동 가능
         */
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.mainFragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)
        addPostButton(navController)

    }
    private fun showBoardPostModal() {
        PostSelectBottomSheetDialog().run {
            setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppBottomSheetDialogTheme)
            show(supportFragmentManager, tag)
        }
    }

    private fun addPostButton(navController: NavController) {
        val menuView = binding.bottomNavigationView.getChildAt(0) as BottomNavigationMenuView
        val menuItemView3 = menuView.getChildAt(2) as BottomNavigationItemView
        val newMenuView = LayoutInflater.from(this).inflate(R.layout.bottom_menu_item3, binding.bottomNavigationView, false)
        menuItemView3.addView(newMenuView)
        binding.bottomNavigationView.setOnItemSelectedListener {
            if (it.itemId == R.id.placeholder) {
                showBoardPostModal()
                false
            }
            else NavigationUI.onNavDestinationSelected(it, navController)
        }
    }

    private fun updateChatBadges(count: Int) {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        setBadge(bottomNavigationView, R.id.chatListFragment, count)
    }

    private fun updateNotificationBadges(count: Int) {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        setBadge(bottomNavigationView, R.id.notificationListFragment, count)
    }

    private fun observeBadgeCounts() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.chatUnreadCount.collectLatest { newCount ->
                        updateChatBadges(newCount)
                    }
                }

                launch {
                    viewModel.notificationUnreadCount.collectLatest { count ->
                        updateNotificationBadges(count)
                    }
                }
            }
        }
    }

    private fun setBadge(bottomNavigationView: BottomNavigationView, itemId: Int, count: Int) {
        val badgeDrawable = bottomNavigationView.getOrCreateBadge(itemId)
        badgeDrawable.backgroundColor = ContextCompat.getColor(this, R.color.colorPrimary)
        badgeDrawable.badgeTextColor = ContextCompat.getColor(this, R.color.white)
        badgeDrawable.isVisible = count > 0
        badgeDrawable.number = count
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            MentoApplication.notificationPermissionPrefs.setChatPermission(true)
            Toast.makeText(this, "알림 권한 승인", Toast.LENGTH_LONG).show()
        } else {
            MentoApplication.notificationPermissionPrefs.setChatPermission(false)
            Toast.makeText(this, "알림 권한 실패", Toast.LENGTH_LONG).show()
        }
    }

    // 사용자가 권한을 거부하면 다시 요청 메세지 띄우기 위해 IntroActivity가 아닌 MainActivity에서 권한 요청
    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // 알림 권한이 있는 경우
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                MentoApplication.notificationPermissionPrefs.setChatPermission(true)
                Toast.makeText(this, "이미 승인된 알림 권한", Toast.LENGTH_LONG).show()

            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // 이전에 사용자가 알림 권한 요청을 거부했을 경우
                AlertDialog.Builder(this)
                    .setTitle("알림 권한 필요")
                    .setMessage("이 앱은 중요한 소식과 업데이트를 알리기 위해 알림 기능이 필요합니다. 알림 권한을 허용해주세요.")
                    .setPositiveButton("권한 설정하기") { dialog, which ->
                        // 권한 요청 다시 진행
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                    .setNegativeButton("나중에") { dialog, which ->
                        // 사용자가 나중에 결정하길 원할 경우 처리
                    }
                    .show()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}