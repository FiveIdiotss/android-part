package com.minhoi.memento

import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.minhoi.memento.base.BaseActivity
import com.minhoi.memento.data.network.APIService
import com.minhoi.memento.data.network.RetrofitClient
import com.minhoi.memento.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding>() {
    private val retrofit = RetrofitClient.getLoggedInInstance().create(APIService::class.java)
    private val retrofitOut = RetrofitClient.getInstance().create(APIService::class.java)
    override val layoutResourceId: Int = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setBottomNavigation()
    }

    private fun setBottomNavigation() {
        /*
        NavController는 onNavDestinationSelected() 호출.
        따라서 MenuItem의 id가 대상의 id와 일치하면 Navcontroller는 그 대상으로 이동 가능
         */
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.bottomNavigationView) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)
    }
}