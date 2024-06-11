package com.minhoi.memento.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.minhoi.memento.MainActivity
import com.minhoi.memento.R
import com.minhoi.memento.base.BaseActivity
import com.minhoi.memento.databinding.ActivityLoginBinding
import com.minhoi.memento.ui.UiState
import com.minhoi.memento.utils.ProgressDialog
import com.minhoi.memento.utils.hideLoading
import com.minhoi.memento.utils.showLoading
import com.minhoi.memento.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>() {

    override val layoutResourceId: Int = R.layout.activity_login

    private val viewModel by viewModels<LoginViewModel>()
    private val progressDialog: ProgressDialog by lazy { ProgressDialog() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel
    }

    override fun initView() {

        setUpToolbar()
        binding.loginBtn.setOnClickListener {
            viewModel.signIn()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loginState.collectLatest {
                    when (it) {
                        is UiState.Empty -> {}
                        is UiState.Loading -> {
                            supportFragmentManager.showLoading()
                        }
                        is UiState.Success -> {
                            supportFragmentManager.hideLoading()
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        }
                        is UiState.Error -> {
                            supportFragmentManager.hideLoading()
                            showToast(it.error!!.message!!)
                        }
                    }
                }
            }
        }
    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.loginToolbar)
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

    private fun showLoading() {
        progressDialog.show(supportFragmentManager, "progress")
    }

    private fun hideLoading() {
        progressDialog.dismiss()
    }

}