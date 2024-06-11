package com.minhoi.memento.ui.question

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
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
        setUpToolbar()

        type = intent.getStringExtra("type")

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
                            binding.toolbarText.text = "내가 작성한 질문글"
                            questionRowAdapter.submitData(it)
                        }
                    }
                    "likeQuestion" -> {
                        viewModel.getLikedQuestions().collectLatest {
                            binding.filterLayout.visibility = View.GONE
                            binding.toolbarText.text = "좋아요 한 질문글"
                            questionRowAdapter.submitData(it)
                        }
                    }
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