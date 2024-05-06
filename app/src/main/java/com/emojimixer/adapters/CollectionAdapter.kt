package com.emojimixer.adapters

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
import com.emojimixer.databinding.ItemCollectionBinding
import com.google.android.material.progressindicator.CircularProgressIndicator

class CollectionAdapter(
    var mContext: Context,
    var listCollection: ArrayList<String>,
    val callBack: (result:String) -> Unit
) :
    RecyclerView.Adapter<CollectionAdapter.ViewHolder>() {




    class ViewHolder(val binding: ItemCollectionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemCollectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    fun updateData(newList: ArrayList<String>) {
        listCollection.clear()
        listCollection.addAll(newList)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return listCollection.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var collection = listCollection[position]
        holder.binding.ivAnimal
        loadEmojiFromUrl(holder.binding.ivAnimal, holder.binding.progressBar, collection)
        holder.itemView.setOnClickListener {
            callBack.invoke(collection)
        }
    }

    private fun loadEmojiFromUrl(
        image: ImageView,
        progressBar: CircularProgressIndicator,
        url: String
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
                        Log.d("url==", url.toString())
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


}