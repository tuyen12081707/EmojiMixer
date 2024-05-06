package com.emojimixer.activities


import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.emojimixer.R
import com.emojimixer.databinding.ActivitySettingBinding
import com.emojimixer.utils.Common
import com.emojimixer.utils.Common.runSelected


class SettingActivity : BaseActivity<ActivitySettingBinding>(ActivitySettingBinding::inflate) {

    var mLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

    }

    override fun initView() {
        binding.tvSound.runSelected()
        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.lnLanguages.setOnClickListener {
            mLauncher.launch(Intent(this, LanguagesActivity::class.java).putExtra("fromHome", true))
        }
        if (Common.getSoundBool(this)) {
            binding.switchSound.setImageResource(R.drawable.ic_on)
        } else {
            binding.switchSound.setImageResource(R.drawable.ic_on)
        }
        binding.lnSound.setOnClickListener {
            if (Common.getSoundBool(this)) {
                binding.switchSound.setImageResource(R.drawable.ic_off)
            } else {
                binding.switchSound.setImageResource(R.drawable.ic_on)
            }
            Common.setSoundBool(this, !Common.getSoundBool(this))

        }
    }


}