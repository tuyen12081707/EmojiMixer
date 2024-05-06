package com.emojimixer.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.emojimixer.R
import com.emojimixer.ads.AdsManager
import com.emojimixer.databinding.ActivityHomeBinding

class HomeActivity : BaseActivity<ActivityHomeBinding>(ActivityHomeBinding::inflate),
    View.OnClickListener {

    var mLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

    }

    override fun initView() {
        binding.btnMerge.setOnClickListener(this)
        binding.tvCollection.setOnClickListener(this)
        binding.tvSetting.setOnClickListener(this)
        AdsManager.loadAndShowNative(this,binding.frNative,AdsManager.NATIVE_HOME)
        AdsManager.loadInter(this,AdsManager.INTER_MERGE)
        AdsManager.loadInter(this,AdsManager.INTER_CHOOSE_ITEM)
    }

    override fun onClick(view: View) {
        if (view.id == R.id.btn_merge) {
            mLauncher.launch(Intent(this,MainActivity::class.java))
        }else if(view.id == R.id.tv_collection){
            mLauncher.launch(Intent(this,CollectionActivity::class.java))
        }else if(view.id==R.id.tv_setting){
            mLauncher.launch(Intent(this,SettingActivity::class.java))
        }
    }
}