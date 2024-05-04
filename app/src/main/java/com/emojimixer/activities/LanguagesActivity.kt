package com.emojimixer.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.widget.Toast
import com.emojimixer.R
import com.emojimixer.adapters.LanguageAdapter
import com.emojimixer.databinding.ActivityLanguagesBinding
import com.emojimixer.utils.Common
import com.emojimixer.utils.Common.gone
import com.emojimixer.utils.Common.visible
import com.jakewharton.rxbinding4.view.clicks
import java.util.concurrent.TimeUnit

class LanguagesActivity:BaseActivity<ActivityLanguagesBinding>(ActivityLanguagesBinding::inflate) {
    companion object {
        var languageKey = ""
    }
    lateinit var language: String
    var flag = R.drawable.english
    var pos = 12
    var adapter: LanguageAdapter? = null
    override fun initView() {
        binding.tvLanguages.isSelected = true
        if (!intent.getBooleanExtra("fromHome", false)) {
            binding.ivBack.gone()
        }else{
            binding.ivBack.visible()
        }
        LanguageAdapter.selected = Common.getPositionLanguage(this)
        flag = Common.getPreLanguageflag(this)
        language = Common.getLang(this).toString()
        loadData()
        buttonDone()
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

    }
    private fun loadData() {
        languageKey = language
        pos = Common.getPositionLanguage(this)
        val languageList = Common.listLanguages(this)


        adapter = LanguageAdapter(object : LanguageAdapter.OnClickListener {
            override fun onClickListener(position: Int, name: String, img: Int) {
                pos = position
                language = name
                flag = img
                adapter?.updatePosition(pos)
            }
        }, this)

        adapter?.updateData(languageList)
        adapter?.updatePosition(pos)
        binding.rcvLanguage.adapter = adapter
    }
    @SuppressLint("CheckResult")
    private fun buttonDone() {
        binding.ivCheck.clicks().throttleFirst(1, TimeUnit.SECONDS).subscribe {

            if (LanguageAdapter.selected != 12) {
                Common.setLang(this, language)
                Common.setPositionLanguage(this, pos)
                Common.setPreLanguageflag(this, flag)
                if (intent.getBooleanExtra("fromHome", false)) {
                    val intent = Intent(this@LanguagesActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                } else {
                    val intent = Intent(this@LanguagesActivity, IntroActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                }

            } else {
                Toast.makeText(this, "Please choose your language!", Toast.LENGTH_SHORT).show()
            }
        }
    }

}