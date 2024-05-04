package com.emojimixer.activities

import android.content.Intent
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.emojimixer.R
import com.emojimixer.adapters.IntroAdapter
import com.emojimixer.databinding.ActivityIntroBinding
import com.emojimixer.model.Introduction

class IntroActivity : BaseActivity<ActivityIntroBinding>(ActivityIntroBinding::inflate) {
    lateinit var pager: ViewPager
    private lateinit var myPageAdapter: PagerAdapter
    var listIntro: MutableList<Introduction> = mutableListOf()

    override fun initView() {
        createListIntro()
        pager = binding.vpIntro
        myPageAdapter = IntroAdapter(this, listIntro)
        pager.adapter = myPageAdapter
        binding.tvNext.setText(R.string.next)
        binding.tvNext.setOnClickListener { view ->
            val currentItem = pager.currentItem
            val nextPage = currentItem + 1
            if (nextPage < myPageAdapter.count) {
                pager.currentItem = nextPage

            } else {
                val intent =
                    Intent(this@IntroActivity, MainActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
        pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    binding.ivDot.setImageResource(R.drawable.ic_dot_1)
                    binding.bgIntro.setImageResource(R.drawable.bg_intro_1)
                } else if (position == 1) {
                    binding.ivDot.setImageResource(R.drawable.ic_dot_2)
                    binding.bgIntro.setImageResource(R.drawable.bg_intro_2)
                } else if (position == 2) {
                    binding.ivDot.setImageResource(R.drawable.ic_dot_3)
                    binding.bgIntro.setImageResource(R.drawable.bg_intro_3)
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
            }

        })
    }
    private fun createListIntro() {
        listIntro.add(
            Introduction(
                R.drawable.img_intro_1,
                R.string.intro_title_1,
            )
        )
        listIntro.add(
            Introduction(
                R.drawable.img_intro_2,
                R.string.intro_title_2,
            )
        )
        listIntro.add(
            Introduction(
                R.drawable.img_intro_3,
                R.string.intro_title_3,
            )
        )
    }
}