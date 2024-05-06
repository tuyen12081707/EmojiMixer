package com.emojimixer.utils

import android.app.Activity
import android.content.Context
import android.view.View
import com.emojimixer.R
import com.emojimixer.model.LanguageModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object Common {
    @JvmStatic
    var totalMilliseconds: Long = 0
    private var timeLast = 0L
    @JvmStatic
    var countInter = 1
    fun getLang(mContext: Context): String? {
        val preferences =
            mContext.getSharedPreferences(mContext.packageName, Context.MODE_MULTI_PROCESS)
        return preferences.getString("KEY_LANG", "en")
    }

    fun singleClick1s(): Boolean {
        val time = System.currentTimeMillis() - timeLast
        if (time > 1000) {
            timeLast = System.currentTimeMillis()
            return true
        }
        return false
    }

    fun View.gone() {
        visibility = View.GONE
    }
    fun View.runSelected() {
        isSelected = true
    }

    fun View.visible() {
        visibility = View.VISIBLE
    }

    fun View.invisible() {
        visibility = View.INVISIBLE
    }

    fun singleClickShort(): Boolean {
        val time = System.currentTimeMillis() - timeLast
        if (time > 500) {
            timeLast = System.currentTimeMillis()
            return true
        }
        return false
    }

    fun setLang(context: Context, open: String?) {
        val preferences =
            context.getSharedPreferences(context.packageName, Context.MODE_MULTI_PROCESS)
        preferences.edit().putString("KEY_LANG", open).apply()
    }

    //    =================language=============================
    fun listLanguages(activity: Activity): ArrayList<LanguageModel> {
        var languageList = ArrayList<LanguageModel>()
        languageList.add(
            LanguageModel(
                R.drawable.english,
                activity.getString(R.string.english),
                "en"
            )
        )
        languageList.add(LanguageModel(R.drawable.hindi, activity.getString(R.string.hindi), "hi"))
        languageList.add(
            LanguageModel(
                R.drawable.spanish,
                activity.getString(R.string.spanish),
                "es"
            )
        )
        languageList.add(
            LanguageModel(
                R.drawable.french,
                activity.getString(R.string.french),
                "fr"
            )
        )
        languageList.add(
            LanguageModel(
                R.drawable.arabic,
                activity.getString(R.string.arabic),
                "ar"
            )
        )
        languageList.add(
            LanguageModel(
                R.drawable.bengali,
                activity.getString(R.string.bengali),
                "bn"
            )
        )
        languageList.add(
            LanguageModel(
                R.drawable.russian,
                activity.getString(R.string.russian),
                "ru"
            )
        )
        languageList.add(
            LanguageModel(
                R.drawable.portuguese,
                activity.getString(R.string.portuguese),
                "pt"
            )
        )
        languageList.add(
            LanguageModel(
                R.drawable.indonesian,
                activity.getString(R.string.indonesian),
                "in"
            )
        )
        languageList.add(
            LanguageModel(
                R.drawable.german,
                activity.getString(R.string.german),
                "de"
            )
        )
        languageList.add(
            LanguageModel(
                R.drawable.italian,
                activity.getString(R.string.italian),
                "it"
            )
        )
        languageList.add(
            LanguageModel(
                R.drawable.korean,
                activity.getString(R.string.korean),
                "ko"
            )
        )
        return languageList
    }

    fun getPreLanguageflag(mContext: Context): Int {
        val preferences =
            mContext.getSharedPreferences(mContext.packageName, Context.MODE_MULTI_PROCESS)
        return preferences.getInt("KEY_FLAG", R.drawable.english)
    }

    fun setPreLanguageflag(context: Context, flag: Int) {
        val preferences =
            context.getSharedPreferences(context.packageName, Context.MODE_MULTI_PROCESS)
        preferences.edit().putInt("KEY_FLAG", flag).apply()
    }

    fun setPositionLanguage(context: Context, open: Int) {
        val preferences =
            context.getSharedPreferences(context.packageName, Context.MODE_MULTI_PROCESS)
        preferences.edit().putInt("KEY_POSITION", open).apply()
    }

    fun getPositionLanguage(mContext: Context): Int {
        val preferences =
            mContext.getSharedPreferences(mContext.packageName, Context.MODE_MULTI_PROCESS)
        return preferences.getInt("KEY_POSITION", 0)
    }



    fun getSoundBool(mContext: Context): Boolean {
        val preferences =
            mContext.getSharedPreferences(mContext.packageName, Context.MODE_MULTI_PROCESS)
        return preferences.getBoolean("KEY_SOUND", true)
    }

    fun setSoundBool(context: Context, open: Boolean) {
        val preferences =
            context.getSharedPreferences(context.packageName, Context.MODE_MULTI_PROCESS)
        preferences.edit().putBoolean("KEY_SOUND", open).apply()
    }

    fun setListCollection(context: Context, list: ArrayList<String>) {
        val preferences =
            context.getSharedPreferences(context.packageName, Context.MODE_MULTI_PROCESS)
        val gson = Gson()
        val json = gson.toJson(list)//converting list to Json
        val editor = preferences.edit()
        editor.putString("KEY_LIST_COLLECTION", json)
        editor.apply()
    }

    fun getListCollection(context: Context): ArrayList<String> {
        try {
            val preferences =
                context.getSharedPreferences(context.packageName, Context.MODE_MULTI_PROCESS)
            val gson = Gson()
            val json = preferences.getString("KEY_LIST_COLLECTION", null)
            val type = object : TypeToken<ArrayList<String>>() {}.type//converting the json to list
            return gson.fromJson(json, type)//returning the list
        } catch (e: Exception) {
            return ArrayList()
        }
    }

}