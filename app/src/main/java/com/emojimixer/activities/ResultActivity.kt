package com.emojimixer.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.emojimixer.R
import com.emojimixer.databinding.ActivityResultBinding
import com.emojimixer.functions.EmojiMixer
import com.emojimixer.functions.RequestNetwork
import com.emojimixer.functions.RequestNetworkController
import com.emojimixer.functions.UIMethods.shadAnim
import com.emojimixer.functions.Utils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.Objects


class ResultActivity : BaseActivity<ActivityResultBinding>(ActivityResultBinding::inflate) {
    private var emote1: String? = null
    private var emote2: String? = null
    private var sharedPref: SharedPreferences? = null
    private var isEnableSHare = false
    private var emojiUrlLocal: String = ""
    private var date = "20210218"

    override fun initView() {
        sharedPref = getSharedPreferences("AppData", MODE_PRIVATE)

        emote1 = intent.getStringExtra("unicode1")

        emote2 = intent.getStringExtra("unicode2")
        if(intent.getStringExtra("date")!=null){
            date = intent.getStringExtra("date").toString()
        }

        Log.d("emote==", emote1.toString())
        Log.d("emote==", emote2.toString())
        mixEmojis(
            emote1!!,
            emote2!!,
            date
        )
        binding.ivHome.setOnClickListener {
            nextActivityClearTag<HomeActivity>(this)
        }
        binding.lnCreateNew.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }

        binding.saveEmoji.setOnClickListener(View.OnClickListener { view: View? ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Utils.saveImage(
                    binding.mixedEmoji,
                    this@ResultActivity,
                    "\uD83D\uDE22",
                    false
                )
            } else {
                if (ContextCompat.checkSelfPermission(
                        this@ResultActivity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    Utils.saveImage(
                        binding.mixedEmoji,
                        this@ResultActivity,
                        "\uD83D\uDE22",
                        false
                    )
                } else {
                    ActivityCompat.requestPermissions(
                        this@ResultActivity,
                        arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        1
                    )
                }
            }
        })
        binding.share.setOnClickListener {
            if (isEnableSHare) {
                downloadAndProcessImage(emojiUrlLocal)
            }

        }
    }
    @SuppressLint("StaticFieldLeak")
    private fun downloadAndProcessImage(imageUrl: String) {
        // Use AsyncTask to download the image in the background
        object : AsyncTask<String, Void, File?>() {
            override fun doInBackground(vararg params: String): File? {
                try {
                    val url = URL(params[0])
                    val connection = url.openConnection() as HttpURLConnection
                    connection.connect()

                    // Create a temporary file to save the image
                    val outputDir = cacheDir
                    val outputFile = File(outputDir, "temp_image_${System.currentTimeMillis()}.jpg")

                    val input = connection.inputStream
                    val output = FileOutputStream(outputFile)

                    val buffer = ByteArray(1024)
                    var bytesRead: Int

                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                    }

                    output.flush()
                    output.close()
                    input.close()

                    return outputFile
                } catch (e: Exception) {
                    // Handle exceptions appropriately
                    return null
                }
            }

            override fun onPostExecute(result: File?) {
                // Process or share the downloaded image
                result?.let {
                    processImage(it)
                }
            }
        }.execute(imageUrl)
    }

    private fun processImage(imageFile: File) {
        // Do something with the image file, e.g., display it, share it, etc.
        // For example, let's share the image
        shareImage(imageFile)
    }

    private fun shareImage(imageFile: File) {
        // Get the URI for the file using FileProvider
        val uri: Uri = FileProvider.getUriForFile(
            this,
            "${packageName}.provider",
            imageFile
        )

        // Create an Intent for sharing
        val share = Intent().apply {
            action = Intent.ACTION_SEND
            type = "image/*"
            putExtra(Intent.EXTRA_SUBJECT, "Subject Here")
            putExtra(Intent.EXTRA_STREAM, uri)
        }

        // Start the activity with a chooser dialog
        startActivity(Intent.createChooser(share, "Share Image"))
    }

    private fun shareLocalImage(file: File) {
//        val uri: Uri = FileProvider.getUriForFile(
//            this,
//            "$packageName.provider",
//            file
//        )
//        val file = File(sharePath)
        if (file.exists()) {
            Log.e("File", "Exists");
        }
        if (file.exists()) Log.e("file", "exits")
        val uri: Uri = FileProvider.getUriForFile(
            this,
            "$packageName.provider",
            file
        )
        val share = Intent()
        share.action = Intent.ACTION_SEND
        share.type = "image/*"
        share.putExtra(Intent.EXTRA_SUBJECT, "Subject Here")
        share.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(Intent.createChooser(share, "Share Image"))
    }

    fun getLocalBitmapUri(imageView: ImageView): Uri? {
        // Extract Bitmap from ImageView drawable
        val drawable = imageView.drawable
        var bmp: Bitmap? = null
        bmp = if (drawable is BitmapDrawable) {
            (imageView.drawable as BitmapDrawable).bitmap
        } else {
            return null
        }
        // Store image to default external storage directory
        var bmpUri: Uri? = null
        try {
            val file = File(
                Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS
                ), "share_image_" + System.currentTimeMillis() + ".png"
            )
            file.parentFile.mkdirs()
            val out = FileOutputStream(file)
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out)
            out.close()
            bmpUri = Uri.fromFile(file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bmpUri
    }


    private fun mixEmojis(emoji1: String, emoji2: String, date: String) {
        binding.progressBar.visibility = View.VISIBLE
        val em = EmojiMixer(emoji1, emoji2, date, this, object : EmojiMixer.EmojiListener {
            override fun onSuccess(emojiUrl: String) {
                isEnableSHare = true
                emojiUrlLocal = emojiUrl
                Log.d("emojiUrl==", emojiUrl)
                setImageFromUrl(binding.mixedEmoji, emojiUrl)
            }

            override fun onFailure(failureReason: String) {
                isEnableSHare = false
                binding.mixedEmoji.setImageResource(R.drawable.sad)
                shouldShowEmoji(true)
            }
        })
        val thread = Thread(em)
        thread.start()
    }

    private fun setImageFromUrl(image: ImageView, url: String) {
        Glide.with(this)
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
                        shouldShowEmoji(true)
                        return false
                    }

                    override fun onResourceReady(
                        p0: Drawable,
                        model: Any,
                        target: Target<Drawable?>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        shouldShowEmoji(true)
                        return false
                    }
                }
            )
            .into(image)
    }

    private fun shouldShowEmoji(shouldShow: Boolean) {
        if (shouldShow) {
            shadAnim(binding.mixedEmoji, "scaleY", 1.0, 300)
            shadAnim(binding.mixedEmoji, "scaleX", 1.0, 300)
            shadAnim(binding.progressBar, "scaleY", 0.0, 300)
            shadAnim(binding.progressBar, "scaleX", 0.0, 300)
        } else {
            shadAnim(binding.mixedEmoji, "scaleY", 0.0, 300)
            shadAnim(binding.mixedEmoji, "scaleX", 0.0, 300)
            shadAnim(binding.progressBar, "scaleY", 1.0, 300)
            shadAnim(binding.progressBar, "scaleX", 1.0, 300)
        }
    }


}