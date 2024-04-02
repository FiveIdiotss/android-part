package com.minhoi.memento.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.minhoi.memento.MainActivity
import com.minhoi.memento.R
import com.minhoi.memento.base.BaseActivity
import com.minhoi.memento.databinding.ActivityLoginBinding
import com.minhoi.memento.ui.UiState
import com.minhoi.memento.utils.ProgressDialog
import com.minhoi.memento.utils.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : BaseActivity<ActivityLoginBinding>() {

    override val layoutResourceId: Int = R.layout.activity_login

    private lateinit var viewModel: LoginViewModel
    private val progressDialog: ProgressDialog by lazy { ProgressDialog() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        binding.viewModel = viewModel
    }

    override fun initView() {

        binding.loginBtn.setOnClickListener {
            viewModel.signIn()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loginState.collectLatest {
                    when (it) {
                        is UiState.Empty -> {}
                        is UiState.Loading -> {
                            showLoading()
                        }
                        is UiState.Success -> {
                            hideLoading()
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        }
                        is UiState.Error -> {
                            hideLoading()
                            showToast("네트워크 오류가 발생하였습니다. 다시 시도해주세요")
                        }
                    }
                }
            }
        }
    }

    private fun showLoading() {
        progressDialog.show(supportFragmentManager, "progress")
    }

    private fun hideLoading() {
        progressDialog.dismiss()
    }

}