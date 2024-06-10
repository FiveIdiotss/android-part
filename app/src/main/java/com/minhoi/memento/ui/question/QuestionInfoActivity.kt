package com.minhoi.memento.ui.question

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.minhoi.memento.R
import com.minhoi.memento.base.BaseActivity
import com.minhoi.memento.databinding.ActivityQuestionInfoBinding
import com.minhoi.memento.ui.UiState
import com.minhoi.memento.ui.adapter.ReplyAdapter
import com.minhoi.memento.utils.hideKeyboard
import com.minhoi.memento.utils.hideLoading
import com.minhoi.memento.utils.setOnSingleClickListener
import com.minhoi.memento.utils.showLoading
import com.minhoi.memento.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class QuestionInfoActivity : BaseActivity<ActivityQuestionInfoBinding>() {
    override val layoutResourceId: Int = R.layout.activity_question_info
    private val viewModel by viewModels<QuestionViewModel>()
    private var questionId: Long = -1
    private val replyAdapter: ReplyAdapter by lazy {
        ReplyAdapter()
    }

    override fun initView() {
        questionId = intent.getLongExtra("questionId", -1L)
        Log.d("QuestionInfoActivity", "initView: $questionId")
        if (questionId == -1L) {
            showToast("일시적인 오류가 발생하였습니다. 다시 시도해주세요.")
            return
        }

        setUpToolbar()
        viewModel.getQuestion(questionId)

        observeQuestionContent()
        observePostReplyState()
        getReplies(questionId)

        binding.replyRv.apply {
            adapter = replyAdapter
            layoutManager = LinearLayoutManager(this@QuestionInfoActivity, LinearLayoutManager.VERTICAL, false)
        }

        binding.inputReply.addTextChangedListener(replyTextWatcher)

        binding.replyUploadBtn.setOnSingleClickListener {
            postReply(questionId, binding.inputReply.text.toString())
            binding.inputReply.setText("")
            binding.inputReply.hideKeyboard()
        }
    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.questionInfoToolbar)
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

    private fun observeQuestionContent() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.questionContentState.collectLatest { state ->
                    when (state) {
                        is UiState.Empty -> {}
                        is UiState.Loading -> supportFragmentManager.showLoading()
                        is UiState.Success -> {
                            supportFragmentManager.hideLoading()
                            binding.questionContent = state.data
                        }
                        is UiState.Error -> {
                            supportFragmentManager.hideLoading()
                            state.error!!.message?.let { showToast(it) }
                        }
                    }
                }
            }
        }
    }

    private fun getReplies(questionId: Long) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getReplies(questionId).collectLatest {
                    replyAdapter.submitData(it)
                }
            }
        }
    }

    private fun observePostReplyState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.replyState.collectLatest { state ->
                    when (state) {
                        is UiState.Empty, UiState.Loading -> {}
                        is UiState.Success -> {
                            viewModel.getQuestion(questionId)
                            replyAdapter.refresh()
                        }
                        is UiState.Error -> {
                            supportFragmentManager.hideLoading()
                            state.error!!.message?.let { showToast(it) }
                        }
                    }
                }
            }
        }
    }

    private fun postReply(questionId: Long, body: String) {
        viewModel.postReply(questionId, body)
    }

    private val replyTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            binding.replyUploadBtn.isVisible = count > 0
        }

        override fun afterTextChanged(s: Editable?) {}
    }

}