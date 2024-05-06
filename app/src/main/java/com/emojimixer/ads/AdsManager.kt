package com.emojimixer.ads

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustAdRevenue
import com.adjust.sdk.AdjustConfig
import com.emojimixer.BuildConfig
import com.emojimixer.R
import com.emojimixer.utils.Common
import com.emojimixer.utils.Common.countInter
import com.emojimixer.utils.Common.gone
import com.emojimixer.utils.Common.visible
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdValue
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.nativead.NativeAd
import com.vapp.admoblibrary.AdsInterCallBack
import com.vapp.admoblibrary.ads.AdCallBackInterLoad
import com.vapp.admoblibrary.ads.AdmobUtils
import com.vapp.admoblibrary.ads.AppOpenManager
import com.vapp.admoblibrary.ads.NativeAdCallback
import com.vapp.admoblibrary.ads.admobnative.enumclass.CollapsibleBanner
import com.vapp.admoblibrary.ads.admobnative.enumclass.GoogleENative
import com.vapp.admoblibrary.ads.model.InterHolder
import com.vapp.admoblibrary.ads.model.NativeHolder
import kotlinx.coroutines.flow.MutableStateFlow


object AdsManager {
    const val AOA_SPLASH = BuildConfig.AOA_SPLASH
    val NATIVE_LANGUAGE = NativeHolder(BuildConfig.NATIVE_LANGUAGE)
    val NATIVE_INTRO = NativeHolder(BuildConfig.NATIVE_INTRO)
    val NATIVE_HOME = NativeHolder(BuildConfig.NATIVE_HOME)
    val BANNER_COLLAP_MERGE = BuildConfig.BANNER_COLLAP_MERGE
    var INTER_MERGE = InterHolder(BuildConfig.INTER_MERGE)
    var INTER_CHOOSE_ITEM = InterHolder(BuildConfig.INTER_CHOOSE_ITEM)
    const val ONRESUME = BuildConfig.ONRESUME

    //    var timeCount=0L
    @JvmStatic
    fun showInterSetTheme(
        context: Context,
        interHolder: InterHolder,
        adListener: AdListener, event: String,
        timeRemote: Int
    ) {
//
//        val time: Long = System.currentTimeMillis() - timeCount
//        if (time < timeRemote) {
//            adListener.onAdClosed()
//            return
//        }

        AdmobUtils.showAdInterstitialWithCallbackNotLoadNew(
            context as Activity, interHolder, 10000,
            object : AdsInterCallBack {
                override fun onAdLoaded() {

                }

                override fun onAdFail(error: String?) {
                    loadInter(context, interHolder)
                    adListener.onAdClosed()
                }


                override fun onPaid(adValue: AdValue?, adUnitAds: String?) {
                    adValue?.let { postRevenueAdjust(it, adUnitAds) }
                }

                override fun onStartAction() {

                }

                override fun onEventClickAdClosed() {
                    loadInter(context, interHolder)
                    adListener.onAdClosed()
                }

                override fun onAdShowed() {
                    Handler().postDelayed({
                        try {
                            AdmobUtils.dismissAdDialog()
                        } catch (_: Exception) {

                        }
                    }, 800)
                }
            }, true
        )
    }

    fun loadNative(context: Context, holder: NativeHolder) {
        AdmobUtils.loadAndGetNativeAds(
            context,
            holder,
            object : NativeAdCallback {
                override fun onLoadedAndGetNativeAd(ad: com.google.android.gms.ads.nativead.NativeAd?) {

                }

                override fun onNativeAdLoaded() {
                }

                override fun onAdFail(error: String) {
                }

                override fun onAdPaid(adValue: AdValue?, adUnitAds: String?) {
                    postRevenueAdjust(adValue!!, adUnitAds)
                }

            })
    }

    fun showNative(activity: Activity, viewGroup: ViewGroup, holder: NativeHolder) {
        if (!AdmobUtils.isNetworkConnected(activity)) {
            viewGroup.visibility = View.GONE
            return
        }
        AdmobUtils.showNativeAdsWithLayout(
            activity,
            holder,
            viewGroup,
            R.layout.ad_template_medium,
            GoogleENative.UNIFIED_MEDIUM,
            object : AdmobUtils.AdsNativeCallBackAdmod {
                override fun NativeFailed(massage: String) {
                    loadNative(activity, holder)
                    viewGroup.gone()
                }

                override fun NativeLoaded() {
                    viewGroup.visible()
                }

                override fun onPaidNative(adValue: AdValue, adUnitAds: String) {
                    postRevenueAdjust(adValue, adUnitAds)

                }
            })
    }

    fun showNativeSmall(activity: Activity, viewGroup: ViewGroup, holder: NativeHolder) {
        if (!AdmobUtils.isNetworkConnected(activity)) {
            viewGroup.visibility = View.GONE
            return
        }
        AdmobUtils.showNativeAdsWithLayout(
            activity,
            holder,
            viewGroup,
            R.layout.ad_template_small,
            GoogleENative.UNIFIED_SMALL,
            object : AdmobUtils.AdsNativeCallBackAdmod {
                override fun NativeFailed(massage: String) {
                    loadNative(activity, holder)
                    viewGroup.gone()
                }

                override fun NativeLoaded() {
                    viewGroup.visible()
                }

                override fun onPaidNative(adValue: AdValue, adUnitAds: String) {
                    postRevenueAdjust(adValue, adUnitAds)

                }
            })
    }

    fun loadAndShowNative(
        activity: Activity,
        nativeAdContainer: ViewGroup,
        nativeHolder: NativeHolder
    ) {
        if (!AdmobUtils.isNetworkConnected(activity)) {
            nativeAdContainer.visibility = View.GONE
            return
        }
        AdmobUtils.loadAndShowNativeAdsWithLayoutAds(activity,
            nativeHolder,
            nativeAdContainer,
            R.layout.ad_template_medium,
            GoogleENative.UNIFIED_MEDIUM,
            object : AdmobUtils.NativeAdCallbackNew {


                override fun onNativeAdLoaded() {
                    Log.d("===nativeload", "true")
                    nativeAdContainer.visibility = View.VISIBLE
                }

                override fun onAdFail(error: String) {
                    nativeAdContainer.visibility = View.GONE
                }

                override fun onAdPaid(adValue: AdValue?, adUnitAds: String?) {
                    postRevenueAdjust(adValue!!, adUnitAds)
                }

                override fun onClickAds() {

                }

                override fun onLoadedAndGetNativeAd(ad: NativeAd?) {

                }
            })
    }

    fun loadAndShowNativeSmall(
        activity: Activity,
        nativeAdContainer: ViewGroup,
        nativeHolder: NativeHolder
    ) {
        if (!AdmobUtils.isNetworkConnected(activity)) {
            nativeAdContainer.visibility = View.GONE
            return
        }
        nativeAdContainer.visibility = View.GONE
        AdmobUtils.loadAndShowNativeAdsWithLayoutAds(activity,
            nativeHolder,
            nativeAdContainer,
            R.layout.ad_template_small,
            GoogleENative.UNIFIED_SMALL,
            object : AdmobUtils.NativeAdCallbackNew {
                override fun onLoadedAndGetNativeAd(ad: com.google.android.gms.ads.nativead.NativeAd?) {
                }

                override fun onNativeAdLoaded() {
                    Log.d("===nativeload", "true")
                    nativeAdContainer.visibility = View.VISIBLE
                }

                override fun onAdFail(error: String) {
                    nativeAdContainer.visibility = View.GONE
                }

                override fun onAdPaid(adValue: AdValue?, adUnitAds: String?) {
                    postRevenueAdjust(adValue!!, adUnitAds)
                }

                override fun onClickAds() {

                }
            })
    }

    fun loadAndShowNativeSmall2(
        activity: Activity,
        nativeAdContainer: ViewGroup,
        nativeHolder: NativeHolder
    ) {
        if (!AdmobUtils.isNetworkConnected(activity)) {
            nativeAdContainer.visibility = View.GONE
            return
        }
        AdmobUtils.loadAndShowNativeAdsWithLayoutAds(activity,
            nativeHolder,
            nativeAdContainer,
            R.layout.ad_template_small,
            GoogleENative.UNIFIED_SMALL,
            object : AdmobUtils.NativeAdCallbackNew {
                override fun onLoadedAndGetNativeAd(ad: NativeAd?) {
                }

                override fun onNativeAdLoaded() {
                    Log.d("===nativeload", "true")
                    nativeAdContainer.visibility = View.VISIBLE
                }

                override fun onAdFail(error: String) {
                    nativeAdContainer.visibility = View.GONE
                }

                override fun onAdPaid(adValue: AdValue?, adUnitAds: String?) {
                    postRevenueAdjust(adValue!!, adUnitAds)
                }

                override fun onClickAds() {

                }
            })
    }

    fun loadInter(context: Context, interHolder: InterHolder) {
        AdmobUtils.loadAndGetAdInterstitial(context, interHolder,
            object : AdCallBackInterLoad {
                override fun onAdClosed() {
                }

                override fun onEventClickAdClosed() {
                }

                override fun onAdShowed() {
                }

                override fun onAdLoaded(
                    interstitialAd: com.google.android.gms.ads.interstitial.InterstitialAd?,
                    isLoading: Boolean
                ) {

                }

                override fun onAdFail(message: String?) {

                }
            }
        )
    }

    @JvmStatic
    fun showAdInter(
        context: Context,
        interHolder: InterHolder,
        callback: AdListener,
        type: String
    ) {
        if (type == "1") {
            if (countInter % 2 == 0) {
                countInter++
            } else {
                countInter++
                callback.onAdClosed()
                return
            }
        }

        AppOpenManager.getInstance().isAppResumeEnabled = true
        AdmobUtils.showAdInterstitialWithCallbackNotLoadNew(
            context as Activity, interHolder, 10000,
            object :
                AdsInterCallBack {
                override fun onStartAction() {

                }

                override fun onEventClickAdClosed() {
                    loadInter(context, interHolder)
                    callback.onAdClosed()
                }

                override fun onAdShowed() {
                    AppOpenManager.getInstance().isAppResumeEnabled = false
                    Handler().postDelayed({
                        try {
                            AdmobUtils.dismissAdDialog()
                        } catch (e: Exception) {

                        }
                    }, 800)
                }

                override fun onAdLoaded() {

                }

                override fun onAdFail(error: String?) {
                    callback.onAdClosed()
                    loadInter(context, interHolder)
                }

                override fun onPaid(adValue: AdValue, adUnitAds: String?) {
                    postRevenueAdjust(adValue, adUnitAds)
                }

            },
            true
        )
    }


    fun showAdsBanner(activity: Activity, adsEnum: String, view: ViewGroup, line: View) {
        if (AdmobUtils.isNetworkConnected(activity)) {
            AdmobUtils.loadAdBanner(
                activity,
                adsEnum,
                view,
                object : AdmobUtils.BannerCallBack {
                    override fun onClickAds() {

                    }

                    override fun onFailed(message: String) {
                        view.visibility = View.GONE
                        line.visibility = View.GONE
                    }

                    override fun onLoad() {
                        view.visibility = View.VISIBLE
                        line.visibility = View.VISIBLE
                    }

                    override fun onPaid(adValue: AdValue?, mAdView: AdView?) {
                        postRevenueAdjust(adValue!!, mAdView?.adUnitId)
                    }

                })
        } else {
            view.visibility = View.GONE
            line.visibility = View.GONE
        }
    }

    @JvmStatic
    fun showAdBannerCollapsible(activity: Activity, adsEnum: String, view: ViewGroup, line: View) {
        if (AdmobUtils.isNetworkConnected(activity)) {
            AdmobUtils.loadAdBannerCollapsible(activity, adsEnum, CollapsibleBanner.BOTTOM, view,
                object : AdmobUtils.BannerCollapsibleAdCallback {
                    override fun onBannerAdLoaded(adSize: AdSize) {
                        view.visibility = View.VISIBLE
                        line.visibility = View.VISIBLE

                        val params: ViewGroup.LayoutParams = view.layoutParams
                        params.height = adSize.getHeightInPixels(activity)
                        view.layoutParams = params
                    }

                    override fun onClickAds() {

                    }

                    override fun onAdFail(message: String) {
                        view.visibility = View.GONE
                        line.visibility = View.GONE
                    }

                    override fun onAdPaid(adValue: AdValue, mAdView: AdView) {
                        postRevenueAdjust(adValue, mAdView.adUnitId)
                    }
                })
        } else {
            view.visibility = View.GONE
            line.visibility = View.GONE
        }
    }

    interface AdListener {
        fun onAdClosed()
    }

    fun postRevenueAdjust(ad: AdValue, adUnit: String?) {
        val adjustAdRevenue = AdjustAdRevenue(AdjustConfig.AD_REVENUE_ADMOB)
        val rev = ad.valueMicros.toDouble() / 1000000
        adjustAdRevenue.setRevenue(rev, ad.currencyCode)
        adjustAdRevenue.setAdRevenueUnit(adUnit)
        Adjust.trackAdRevenue(adjustAdRevenue)
    }
}