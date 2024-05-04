package com.emojimixer.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.createBitmap
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.emojimixer.R
import com.emojimixer.model.Introduction


class IntroAdapter(var mContext: Context, var listIntro: MutableList<Introduction>) :
    PagerAdapter() {


    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val intro: Introduction = listIntro[position]
        val view: View =
            LayoutInflater.from(mContext).inflate(R.layout.item_view_pager, container, false)
        val tvTitle = view.findViewById<TextView>(R.id.tv_title)
        val ivIntro = view.findViewById<ImageView>(R.id.iv_intro)
        Glide.with(mContext).load(intro.imageId).into(ivIntro)
        tvTitle.setText(intro.title)
        container.addView(view)
        return view
    }

    override fun getCount(): Int {
        return listIntro.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}