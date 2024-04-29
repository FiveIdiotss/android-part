package com.minhoi.memento

import android.util.Log
import android.view.LayoutInflater
import androidx.activity.viewModels
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
import com.minhoi.memento.ui.board.MentorPostActivity
import com.minhoi.memento.ui.home.HomeViewModel
import com.minhoi.memento.ui.home.PostSelectBottomSheetDialog

class MainActivity : BaseActivity<ActivityMainBinding>() {
    override val layoutResourceId: Int = R.layout.activity_main
    private val viewModel by viewModels<HomeViewModel>()

    override fun initView() {
        setBottomNavigation()

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
}