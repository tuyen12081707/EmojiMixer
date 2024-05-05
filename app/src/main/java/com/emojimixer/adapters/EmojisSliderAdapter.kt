package com.emojimixer.adapters

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.emojimixer.R
import com.google.android.material.progressindicator.CircularProgressIndicator

class EmojisSliderAdapter(
    private var data: ArrayList<HashMap<String, Any>>,
    private val mContext: Context,
    private var type:Int,
    private val callBack:ICallBack
) : RecyclerView.Adapter<EmojisSliderAdapter.ViewHolder>() {
    init {
        mContext.getSharedPreferences("AppData", Activity.MODE_PRIVATE)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.emojis_slider_item, parent, false)
        )
    }
    fun updateData(newArray:ArrayList<HashMap<String, Any>>){
        data = newArray
        notifyDataSetChanged()
    }
    fun updateType(newType:Int){
        type = newType
        notifyDataSetChanged()
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val view = holder.itemView
        val unicode = data[position]["emojiUnicode"]!!.toString()
        Log.d("unicode==",unicode)
        val date = data[position]["date"]!!.toString()
        val emojiURL = "https://ilyassesalama.github.io/EmojiMixer/emojis/supported_emojis_png/$unicode.png"
        Log.d(unicode.toString()+"==","${getDrawableIdByName(unicode)}+==")
        loadEmojiFromUrl(holder.emoji, holder.progressBar, getDrawableIdByName(unicode))

        view.setOnClickListener {
            if(type==1){
                callBack.clickItem(emojiURL,position,unicode,date)

            }else if(type==2){
                callBack.clickItem2(emojiURL,position,unicode)
            }
        }
    }
    private fun getDrawableIdByName(resourceName: String): Int {
        return mContext.resources.getIdentifier(resourceName, "drawable", mContext.packageName)
    }
    override fun getItemCount(): Int {
        return data.size
    }
     interface ICallBack{
        fun clickItem(url: String, position: Int,unicode:String,date:String)
        fun clickItem2(url: String, position: Int,unicode:String)
    }



    private fun loadEmojiFromUrl(
        image: ImageView,
        progressBar: CircularProgressIndicator,
        url: Int
    ) {
        Glide.with(mContext)
            .load(url)
            .fitCenter()
            .transition(DrawableTransitionOptions.withCrossFade())
            .listener(
                object : RequestListener<Drawable?> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        p1: Any?,
                        target: Target<Drawable?>,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.d("url==",url.toString())

                        progressBar.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        p0: Drawable,
                        model: Any,
                        target: Target<Drawable?>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.visibility = View.GONE
                        return false
                    }
                }
            )
            .into(image)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var emoji: ImageView
        var progressBar: CircularProgressIndicator

        init {
            emoji = itemView.findViewById(R.id.emoji)
            progressBar = itemView.findViewById(R.id.progressBar)
        }
    }

}