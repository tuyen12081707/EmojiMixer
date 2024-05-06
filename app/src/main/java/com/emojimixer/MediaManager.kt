package com.emojimixer


import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.os.Handler

class MediaManager private constructor() {
    var player: MediaPlayer? = null
    var isPlaying = false

    companion object {
        private var instance: MediaManager? = null
        val getInstance: MediaManager
            get() {
                if (instance == null) {
                    instance = MediaManager()
                }
                return instance as MediaManager
            }
    }

    fun playSound(context: Context, soundId: String, duration: Int) {
        stopSound() // Stop the currently playing sound before playing a new one
        try {
            context.assets.openFd(soundId).use { afd ->
                player = MediaPlayer().apply {
                    setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                    prepare()
                    start()
                }
                isPlaying = true
            }
        } catch (_: Exception) {
        }
    }

    fun playSound(context: Context, soundId: String) {
        stopSound() // Stop the currently playing sound before playing a new one
        try {
            context.assets.openFd(soundId).use { afd ->
                player = MediaPlayer().apply {
                    setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                    prepare()
                    start()
                }
                isPlaying = true

            }
        } catch (ignored: Exception) {
        }
    }

    fun stopSound() {
        try {
            if (isPlaying && player != null) {
                player?.apply {
                    pause()
                    seekTo(0)
                    isLooping = false
                    release()
                }
                isPlaying = false
            }
        } catch (e: Exception) {
        }
    }
}
