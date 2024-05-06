package com.emojimixer.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.emojimixer.R
import com.emojimixer.ads.AdsManager
import com.emojimixer.databinding.ActivitySplashBinding
import com.google.android.gms.ads.AdValue
import com.vapp.admoblibrary.ads.AOAManager
import com.vapp.admoblibrary.ads.AdmobUtils
import com.vapp.admoblibrary.ads.AppOpenManager
import com.vapp.admoblibrary.cmp.GoogleMobileAdsConsentManager
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.system.exitProcess

class SplashActivity : BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate) {
    private var aoaManager: AOAManager? = null
    private var isFailed = false

    private lateinit var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager
    private var isMobileAdsInitializeCalled = AtomicBoolean(false)
    override fun initView() {
        if (!isTaskRoot
            && intent.hasCategory(Intent.CATEGORY_LAUNCHER)
            && intent.action != null
            && intent.action == Intent.ACTION_MAIN
        ) {
            finish()
            return
        }
        if (AdmobUtils.isNetworkConnected(this)) {
            setupCMP()
        } else {
            Handler().postDelayed({
                nextActivityClearTag<LanguagesActivity>(this)
            }, 3000)
        }
    }

    fun setupCMP() {
        googleMobileAdsConsentManager = GoogleMobileAdsConsentManager(this)
        googleMobileAdsConsentManager.gatherConsent { error ->
            error?.let {
                // Consent not obtained in current session.
                initializeMobileAdsSdk()
            }

            if (googleMobileAdsConsentManager.canRequestAds) {
                initializeMobileAdsSdk()
            }
        }

    }

    private fun initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.get()) {
            //start action
            return
        }
        isMobileAdsInitializeCalled.set(true)
        AdmobUtils.initAdmob(this, 10000, isDebug = true, isEnableAds = true)
        if (android.os.Build.VERSION.SDK_INT != android.os.Build.VERSION_CODES.P) {
            AppOpenManager.getInstance().init(application, AdsManager.ONRESUME)
            AppOpenManager.getInstance().disableAppResumeWithActivity(SplashActivity::class.java)
        }
        loadAOA()
    }

    private fun loadAOA() {
        aoaManager = AOAManager(
            this,
            AdsManager.AOA_SPLASH,
            20000,
            object : AOAManager.AppOpenAdsListener {
                override fun onAdPaid(adValue: AdValue, adUnitAds: String) {
                    adValue.let { AdsManager.postRevenueAdjust(it, adUnitAds) }
                }

                override fun onAdsClose() {
                    nextActivityClearTag<LanguagesActivity>(this@SplashActivity)
                }

                override fun onAdsFailed(message: String) {
                    isFailed = true
                    nextActivityClearTag<LanguagesActivity>(this@SplashActivity)
                }
            })
        aoaManager?.loadAndShowAoA()
        AdsManager.loadNative(this, AdsManager.NATIVE_LANGUAGE)
    }

    override fun onResume() {
        super.onResume()
        if (isFailed) {
            nextActivityClearTag<LanguagesActivity>(this)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        exitProcess(0)
    }
}