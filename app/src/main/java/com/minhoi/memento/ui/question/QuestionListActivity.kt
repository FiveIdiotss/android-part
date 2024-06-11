package com.minhoi.memento.ui.question

import android.content.Intent
import android.view.Menu
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.minhoi.memento.R
import com.minhoi.memento.base.BaseActivity
import com.minhoi.memento.databinding.ActivityQuestionListBinding
import com.minhoi.memento.ui.SelectBoardCategoryBottomSheetDialog
import com.minhoi.memento.ui.adapter.BoardLoadStateAdapter
import com.minhoi.memento.ui.adapter.QuestionRowAdapter
import com.minhoi.memento.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class QuestionListActivity : BaseActivity<ActivityQuestionListBinding>() {
    override val layoutResourceId: Int = R.layout.activity_question_list
    private val viewModel by viewModels<QuestionViewModel>()
    private var type: String? = null
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
        type = intent.getStringExtra("type")
        when (type) {
            "wholeQuestion" -> setupToolbar("질문 글 목록")
            "myQuestion" -> setupToolbar("내가 작성한 질문")
            "likeQuestion" -> setupToolbar("좋아요 표시한 질문")
        }

        binding.questionRv.apply {
            adapter = questionRowAdapter.withLoadStateFooter(BoardLoadStateAdapter())
            layoutManager =
                LinearLayoutManager(this@QuestionListActivity, LinearLayoutManager.VERTICAL, false)
        }

        binding.toPostQuestionBtn.setOnSingleClickListener {
            startActivity(Intent(this, QuestionPostActivity::class.java))
        }

        binding.filterSchoolCheckBox.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setSchoolFilter(isChecked)
        }

        binding.filterCategoryBtn.setOnSingleClickListener {
            SelectBoardCategoryBottomSheetDialog().apply {
                selectCategory(object :
                    SelectBoardCategoryBottomSheetDialog.OnCategorySelectedListener {
                    override fun onCategorySelected(category: String) {
                        binding.filterCategoryBtn.text = category
                        when (category) {
                            "모든 단과대" -> viewModel.setCategoryFilter(null)
                            else -> viewModel.setCategoryFilter(category)
                        }
                    }
                })
            }.show(supportFragmentManager, "selectCategoryFragment")
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                when (type) {
                    "wholeQuestion" -> {
                        viewModel.getQuestions().collectLatest {
                            questionRowAdapter.submitData(it)
                        }
                    }
                    "myQuestion" -> {
                        viewModel.getMyQuestions().collectLatest {
                            binding.filterLayout.visibility = View.GONE
                            questionRowAdapter.submitData(it)
                        }
                    }
                    "likeQuestion" -> {
                        viewModel.getLikedQuestions().collectLatest {
                            binding.filterLayout.visibility = View.GONE
                            questionRowAdapter.submitData(it)
                        }
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (type != "wholeQuestion") return false
        menuInflater.inflate(R.menu.boardlist_toolbar_menu, menu)
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as? SearchView

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.setSearchQuery(newText)
                return true
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

}