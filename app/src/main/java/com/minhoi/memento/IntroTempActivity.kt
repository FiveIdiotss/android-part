package com.minhoi.memento

import androidx.viewpager2.widget.ViewPager2
import com.minhoi.memento.adapter.IntroPagerAdapter
import com.minhoi.memento.base.BaseActivity
import com.minhoi.memento.databinding.ActivityIntroTempBinding
import com.minhoi.memento.utils.setOnSingleClickListener

class IntroTempActivity : BaseActivity<ActivityIntroTempBinding>() {
    override val layoutResourceId: Int = R.layout.activity_intro_temp
    private val pagerList = mutableListOf<String>()
    private lateinit var introPagerAdapter: IntroPagerAdapter

    override fun initView() {
        pagerList.add("1")
        pagerList.add("2")
        pagerList.add("3")

        introPagerAdapter = IntroPagerAdapter(pagerList)
        setUpViewPager()

        binding.startAppBtn.setOnSingleClickListener {
            val introBottomSheetDialog = IntroBottomSheetDialog()
            introBottomSheetDialog.show(supportFragmentManager, introBottomSheetDialog.tag)
        }
    }

    private fun setUpViewPager() {
        binding.introViewPager.apply {
            adapter = introPagerAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            binding.introDotsIndicator.attachTo(this)
        }
    }
}