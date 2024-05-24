package com.minhoi.memento.ui.question

import android.content.Intent
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.minhoi.memento.R
import com.minhoi.memento.adapter.BoardLoadStateAdapter
import com.minhoi.memento.adapter.QuestionRowAdapter
import com.minhoi.memento.base.BaseActivity
import com.minhoi.memento.databinding.ActivityQuestionListBinding
import com.minhoi.memento.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class QuestionListActivity : BaseActivity<ActivityQuestionListBinding>() {
    override val layoutResourceId: Int = R.layout.activity_question_list
    private val viewModel by viewModels<QuestionViewModel>()
    private val questionRowAdapter: QuestionRowAdapter by lazy {
        QuestionRowAdapter() {
            startActivity(
                Intent(this, QuestionInfoActivity::class.java).putExtra(
                    "questionId", it
                )
            )
        }
    }

    override fun initView() {
        setUpToolbar()

        binding.questionRv.apply {
            adapter = questionRowAdapter.withLoadStateFooter(BoardLoadStateAdapter())
            layoutManager = LinearLayoutManager(this@QuestionListActivity, LinearLayoutManager.VERTICAL, false)
        }

        binding.toPostQuestionBtn.setOnSingleClickListener {
            startActivity(Intent(this, QuestionPostActivity::class.java))
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getQuestions(20).collectLatest {
                    questionRowAdapter.submitData(it)
                }
            }
        }
    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.questionToolbar)
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

}