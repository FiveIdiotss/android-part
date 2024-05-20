package com.minhoi.memento.ui.board

import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.minhoi.memento.R
import com.minhoi.memento.base.BaseActivity
import com.minhoi.memento.databinding.ActivityApplyMentoringBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ApplyMentoringActivity : BaseActivity<ActivityApplyMentoringBinding>() {

    override val layoutResourceId: Int = R.layout.activity_apply_mentoring
    private val viewModel: BoardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpNavController()
    }

    override fun initView() {
        val intent = intent
        val boardId = intent.getLongExtra("boardId", -1)
        viewModel.getBoardContent(boardId)
    }

    override fun onSupportNavigateUp(): Boolean {
        if (supportFragmentManager.popBackStackImmediate()) {
            return true
        }
        finish()
        return super.onSupportNavigateUp()
    }

    /*
       NavHostFragment와 toolbar를 연결하는 함수 (모든 Fragment에 뒤로가기 버튼 구성)
    */
    private fun setUpNavController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.apply_nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(
            topLevelDestinationIds = setOf(),
            fallbackOnNavigateUpListener = ::onSupportNavigateUp
        )
        binding.applyToolbar.setupWithNavController(navController, appBarConfiguration)
    }
}