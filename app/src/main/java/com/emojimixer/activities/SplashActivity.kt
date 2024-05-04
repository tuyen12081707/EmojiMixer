package com.emojimixer.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.emojimixer.R
import com.emojimixer.databinding.ActivitySplashBinding

class SplashActivity : BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate) {
    override fun initView() {

        Handler().postDelayed({
            nextActivityClearTag<LanguagesActivity>(this)
        }, 3000)
    }

}