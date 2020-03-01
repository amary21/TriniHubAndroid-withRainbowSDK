package com.amary21.trinihub.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager


@Suppress("DEPRECATION")
object UserPreferences {

    private lateinit var preferences: SharedPreferences

    fun isLogin(context: Context?): Boolean {
        preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getBoolean("login", false)
    }

    fun hasLogin(context: Context?) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context)
        preferences.edit().putBoolean("login", true).apply()
    }

    fun logout(context: Context?) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context)
        preferences.edit().clear().apply()
    }

    fun getTokeUser(context: Context?): String? {
        preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString("token", "")
    }

    fun setTokenUser(context: Context?, token: String?) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context)
        preferences.edit().putString("token", token).apply()
    }

    fun getUserId(context: Context?): String? {
        preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString("userId", "")
    }

    fun setUserId(context: Context?, userId: String?) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context)
        preferences.edit().putString("userId", userId).apply()
    }

    fun getEmailId(context: Context?) : String?{
        preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString("emailId", "")
    }

    fun setEmailId(context: Context?, emailId: String){
        preferences = PreferenceManager.getDefaultSharedPreferences(context)
        preferences.edit().putString("emailId", emailId).apply()
    }

    fun getPasswordId(context: Context?) : String?{
        preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString("passId", "")
    }

    fun setPasswordId(context: Context?, passId : String){
        preferences = PreferenceManager.getDefaultSharedPreferences(context)
        preferences.edit().putString("passId", passId).apply()
    }
}