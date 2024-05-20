package com.minhoi.memento.ui.question

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.minhoi.memento.R
import com.minhoi.memento.base.BaseActivity
import com.minhoi.memento.databinding.ActivityQuestionPostBinding
import com.minhoi.memento.ui.UiState
import com.minhoi.memento.utils.hideLoading
import com.minhoi.memento.utils.setOnSingleClickListener
import com.minhoi.memento.utils.showLoading
import com.minhoi.memento.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class QuestionPostActivity : BaseActivity<ActivityQuestionPostBinding>() {
    override val layoutResourceId: Int = R.layout.activity_question_post
    private val viewModel by viewModels<QuestionViewModel>()

    override fun initView() {
        setUpToolbar()
        observePostQuestionState()

        binding.inputQuestionContent.addTextChangedListener(questionLengthTextWatcher)

        binding.postQuestionBtn.setOnSingleClickListener {
            if (!checkQuestionTitle()) {
                showToast("제목을 입력해주세요")
                return@setOnSingleClickListener
            }

            if (!checkQuestionContent()) {
                showToast("질문 내용을 입력해주세요")
                return@setOnSingleClickListener
            }

            postQuestion()
        }
    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.questionPostToolbar)
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

    private fun postQuestion() {
        viewModel.postQuestion(
            binding.inputQuestionTitle.text.toString(),
            binding.inputQuestionContent.text.toString()
        )
    }

    private fun checkQuestionTitle(): Boolean {
        return binding.inputQuestionTitle.text.length > 2
    }

    private fun checkQuestionContent(): Boolean {
        return binding.inputQuestionContent.text.length > 10
    }

    private fun observePostQuestionState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.postQuestionState.collectLatest { state ->
                    when (state) {
                        is UiState.Empty -> {}
                        is UiState.Loading -> {
                            supportFragmentManager.showLoading()
                        }

                        is UiState.Success -> {
                            supportFragmentManager.hideLoading()
                            showToast("게시글 등록이 완료되었습니다.")
                            finish()
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

    private val questionLengthTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        @SuppressLint("SetTextI18n")
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            binding.inputQuestionContentLength.text = "${s?.length ?: 0} / $MAX_LINE"
            when (s?.length) {
                MAX_LINE -> {
                    binding.inputQuestionContentLength.setTextColor(
                        ContextCompat.getColor(
                            this@QuestionPostActivity,
                            R.color.red
                        )
                    )
                    binding.inputQuestionContent.background = ContextCompat.getDrawable(
                        this@QuestionPostActivity,
                        R.drawable.round_corner_red_color
                    )
                }

                else -> {
                    binding.inputQuestionContentLength.setTextColor(
                        ContextCompat.getColor(
                            this@QuestionPostActivity,
                            R.color.black
                        )
                    )
                    binding.inputQuestionContent.background = ContextCompat.getDrawable(
                        this@QuestionPostActivity,
                        R.drawable.round_corner_black_color
                    )
                }
            }
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    companion object {
        private const val MAX_LINE = 200
    }
}