package com.emojimixer.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.emojimixer.R
import com.emojimixer.model.LanguageModel

class LanguageAdapter(val onClickListener: OnClickListener, val context: Context) :
    RecyclerView.Adapter<LanguageAdapter.ViewHolder>() {

    companion object {
        var selected = 0
    }

    var languageList: ArrayList<LanguageModel> = ArrayList()


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var img = itemView.findViewById<ImageView>(R.id.language_icon)
        var name = itemView.findViewById<TextView>(R.id.language_name)
        var backgr = itemView.findViewById<RelativeLayout>(R.id.language)
        var checkBox = itemView.findViewById<ImageView>(R.id.iv_checkbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_languages, parent, false)
        )
    }

    interface OnClickListener {
        fun onClickListener(position: Int, name: String, img: Int)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(data: ArrayList<LanguageModel>) {
        languageList.clear()
        languageList.addAll(data)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updatePosition(position: Int) {
        selected = position
        notifyDataSetChanged()
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val language = languageList[position]
        if (selected == position) {
            holder.backgr.setBackgroundResource(R.drawable.bg_language_active)
            holder.checkBox.setImageResource(R.drawable.ic_checked)
        } else {
            holder.backgr.setBackgroundResource(R.drawable.bg_language_un_active)
            holder.checkBox.setImageResource(R.drawable.ic_uncheck)
        }


        holder.img.setImageResource(language.img)
        holder.name.text = language.name
        holder.name.isSelected = true

        holder.itemView.setOnClickListener {
            onClickListener.onClickListener(position, language.key, language.img)
        }
    }

    override fun getItemCount(): Int {
        return 12
    }
}