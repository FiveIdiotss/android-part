    package com.minhoi.memento.ui.join

    import android.os.Bundle
    import androidx.lifecycle.ViewModelProvider
    import androidx.navigation.NavController
    import androidx.navigation.findNavController
    import com.minhoi.memento.R
    import com.minhoi.memento.base.BaseActivity
    import com.minhoi.memento.databinding.ActivityJoinBinding

    class JoinActivity : BaseActivity<ActivityJoinBinding>() {

        override val layoutResourceId: Int = R.layout.activity_join

        private lateinit var viewModel: JoinViewModel
        private lateinit var navController: NavController
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            viewModel = ViewModelProvider(this)[JoinViewModel::class.java]

        }

        override fun initView() {

        }

        override fun onSupportNavigateUp(): Boolean {
            navController = findNavController(R.id.fragmentContainerView)
            return navController.navigateUp() || super.onSupportNavigateUp()
        }
    }