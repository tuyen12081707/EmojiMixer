package com.emojimixer.activities

import android.content.Intent
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.emojimixer.adapters.CollectionAdapter
import com.emojimixer.databinding.ActivityCollectionBinding
import com.emojimixer.utils.Common
import com.emojimixer.utils.Common.gone
import com.emojimixer.utils.Common.visible

class CollectionActivity : BaseActivity<ActivityCollectionBinding>(ActivityCollectionBinding::inflate) {
    private var unicode1 = "u1f604"
    private var unicode2 = "u1f422"
    var newDate = "20210218"

    var mLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

    }
    override fun initView() {
        var listCollection = Common.getListCollection(this)
        binding.ivBack.setOnClickListener {
            finish()
        }
        if(listCollection.isEmpty()){
            binding.lnEmpty.visible()
            binding.rcvCollection.gone()
        }else{
            binding.lnEmpty.gone()
            binding.rcvCollection.visible()
        }
        var collectionAdapter = CollectionAdapter(this,listCollection){
            if(it!=null){
                parseEmojiURL(it)
                val intent: Intent = Intent(this, ResultCollectionActivity::class.java)
                    .putExtra("unicode1", unicode1)
                    .putExtra("unicode2", unicode2)
                    .putExtra("date", newDate)
                    .putExtra("collection",true)
                mLauncher.launch(intent)
            }

        }
        binding.rcvCollection.adapter = collectionAdapter

        listCollection.map {
            Log.d("list===", it)
        }
    }

    fun parseEmojiURL(url: String){
        val parts = url.split('/')
        val date = parts[6]
        val unicodeParts = parts[8].substringBeforeLast('.').split('_')
        var unicode1 = unicodeParts[0]
        var unicode2 = unicodeParts[1]
        this.newDate = date
        this.unicode1 = unicode1
        this.unicode2 = unicode2
    }

}