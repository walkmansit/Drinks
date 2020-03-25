package com.office14.coffeedose.repository

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.appcompat.app.AppCompatDelegate
import com.office14.coffeedose.CoffeeDoseApplication

object PreferencesRepository {

    private const val APP_THEME_KEY = "APP_THEME_KEY"
    //private const val ORDER_ID_KEY = "ORDER_ID_KEY"
    private const val BASE_URL_KEY = "BASE_URL_KEY"
    private const val NAVIGATE_TO_ORDER_AWAITING_FRAGMENT_KEY = "NAVIGATE_TO_ORDER_AWAITING_FRAGMENT_KEY"
    private const val GOOGLE_AUTH_TOKEN_KEY = "GOOGLE_AUTH_TOKEN_KEY"
    private const val FIREBASE_AUTH_ID_TOKEN_KEY = "FIREBASE_AUTH_ID_TOKEN_KEY"
    private const val FIREBASE_MESSAGE_REG_TOKEN_KEY = "FIREBASE_MESSAGE_REG_TOKEN_KEY"
    const val EMPTY_STRING = "not initialized"

    private const val BASE_URL = "http://10.0.2.2:5000/api/"

    private val prefs : SharedPreferences by lazy {
        val ctx = CoffeeDoseApplication.applicationContext()
        PreferenceManager.getDefaultSharedPreferences(ctx)
    }

    fun saveBaseUrl(url:String){
        putValue(BASE_URL_KEY to url)
    }

    fun getBaseUrl() = prefs.getString(BASE_URL_KEY,BASE_URL)

    fun saveIdToken(token:String){
        putValue(FIREBASE_AUTH_ID_TOKEN_KEY to token)
    }

    fun getIdToken() = prefs.getString(FIREBASE_AUTH_ID_TOKEN_KEY,EMPTY_STRING)

    fun saveFcmRegToken(token:String) = putValue(FIREBASE_MESSAGE_REG_TOKEN_KEY to token)

    fun getFcmRegToken() = prefs.getString(FIREBASE_MESSAGE_REG_TOKEN_KEY,EMPTY_STRING)

    /*fun getLastOrderId() = prefs.getInt(ORDER_ID_KEY,-1)

    fun saveLastOrderId(orderId:Int) = putValue(ORDER_ID_KEY to orderId)*/

    fun getNavigateToOrderAwaitFrag() = prefs.getBoolean(NAVIGATE_TO_ORDER_AWAITING_FRAGMENT_KEY,false)

    fun saveNavigateToOrderAwaitFrag(nav:Boolean) = putValue(NAVIGATE_TO_ORDER_AWAITING_FRAGMENT_KEY to nav)

    fun saveAppTheme(theme: Int) = putValue(APP_THEME_KEY to theme)

    fun getAppTheme(): Int = prefs.getInt(APP_THEME_KEY, AppCompatDelegate.MODE_NIGHT_NO)

    fun saveGoogleToken(token:String) = putValue(GOOGLE_AUTH_TOKEN_KEY to token)

    fun getGoogleToken() = prefs.getString(GOOGLE_AUTH_TOKEN_KEY,EMPTY_STRING)


    private fun putValue(pair : Pair<String, Any>) = with(prefs.edit()){
        val key = pair.first
        val value = pair.second

        when (value){
            is String -> putString(key, value)
            is Int -> putInt(key, value)
            is Boolean -> putBoolean(key, value)
            is Long -> putLong(key, value)
            is Float -> putFloat(key, value)
            else -> error("Only primitives types can be stored")
        }

        apply()
    }
}