package com.emojimixer
import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LifecycleObserver
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustConfig
import com.adjust.sdk.LogLevel
import com.emojimixer.utils.Common
import java.util.Locale

class App : Application(), LifecycleObserver, Application.ActivityLifecycleCallbacks {
    override fun onCreate() {
        super.onCreate()
        initAdjust()

    }

    private fun initAdjust() {
        val config =
            AdjustConfig(applicationContext, getString(R.string.adjust_token), AdjustConfig.ENVIRONMENT_PRODUCTION)
        config.setLogLevel(LogLevel.WARN)
        Adjust.onCreate(config)
        registerActivityLifecycleCallbacks(AdjustLifecycleCallbacks())
    }

    private class AdjustLifecycleCallbacks : ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
            val langCode: String = Common.getLang(activity).toString()
            val locale = Locale(langCode)
            Locale.setDefault(locale)
            val resource = activity.resources
            val configuration = resource.configuration
            configuration.setLocale(locale)
            resource.updateConfiguration(configuration, resource.displayMetrics)
        }
        override fun onActivityStarted(activity: Activity) {}
        override fun onActivityResumed(activity: Activity) {
            Adjust.onResume()
        }

        override fun onActivityPaused(activity: Activity) {
            Adjust.onPause()
        }

        override fun onActivityStopped(activity: Activity) {}
        override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {}
        override fun onActivityDestroyed(activity: Activity) {}
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {

    }

    override fun onActivityStarted(p0: Activity) {
    }

    override fun onActivityResumed(p0: Activity) {
    }

    override fun onActivityPaused(p0: Activity) {
    }

    override fun onActivityStopped(p0: Activity) {
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
    }

    override fun onActivityDestroyed(p0: Activity) {
    }

}
