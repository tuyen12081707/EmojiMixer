package com.emojimixer.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.emojimixer.R
import com.emojimixer.databinding.ActivityOnBoardingBinding

class OnBoardingActivity :
    BaseActivity<ActivityOnBoardingBinding>(ActivityOnBoardingBinding::inflate) {
    override fun initView() {
        binding.ivLetGo.setOnClickListener {
            nextActivityClearTag<HomeActivity>(this)
        }
    }

}