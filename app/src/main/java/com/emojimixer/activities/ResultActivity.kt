package com.emojimixer.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.emojimixer.MediaManager.Companion.getInstance
import com.emojimixer.R
import com.emojimixer.ads.AdsManager
import com.emojimixer.databinding.ActivityResultBinding
import com.emojimixer.functions.EmojiMixer
import com.emojimixer.functions.UIMethods.shadAnim
import com.emojimixer.functions.Utils
import com.emojimixer.utils.Common
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL


class ResultActivity : BaseActivity<ActivityResultBinding>(ActivityResultBinding::inflate) {
    private var emote1: String? = null
    private var emote2: String? = null
    private var sharedPref: SharedPreferences? = null
    private var isEnableSHare = false
    private var emojiUrlLocal: String = ""
    private var date = "20210218"

    override fun initView() {
        sharedPref = getSharedPreferences("AppData", MODE_PRIVATE)
        if(Common.getSoundBool(this)){
            getInstance.playSound(this@ResultActivity, "sound/win.mp3")
        }

        emote1 = intent.getStringExtra("unicode1")

        emote2 = intent.getStringExtra("unicode2")
        if (intent.getStringExtra("date") != null) {
            date = intent.getStringExtra("date").toString()
        }
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

        binding.saveEmoji.setOnClickListener {
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
        }
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


    private fun mixEmojis(emoji1: String, emoji2: String, date: String) {
        binding.progressBar.visibility = View.VISIBLE
        val em = EmojiMixer(emoji1, emoji2, date, this, object : EmojiMixer.EmojiListener {
            override fun onSuccess(emojiUrl: String) {
                isEnableSHare = true
                emojiUrlLocal = emojiUrl
                Log.d("emojiUrl==", emojiUrl)
                var listCollection = Common.getListCollection(this@ResultActivity)
                listCollection.add(emojiUrl)
                Common.setListCollection(this@ResultActivity,listCollection)
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