package com.office14.coffeedose.plugins

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.office14.coffeedose.CoffeeDoseApplication
import com.office14.coffeedose.domain.entity.UserA
import com.office14.coffeedose.domain.repository.PreferencesRepository
import java.util.*

object PreferencesRepositoryImpl : PreferencesRepository {

    private const val PREFS_NAME = "coffeedose_settings"

    private const val APP_THEME_KEY = "APP_THEME_KEY"
    private const val DEVICE_ID = "DEVICE_ID"

    //private const val ORDER_ID_KEY = "ORDER_ID_KEY"
    private const val BASE_URL_KEY = "BASE_URL_KEY"
    private const val NAVIGATE_TO_ORDER_AWAITING_FRAGMENT_KEY =
        "NAVIGATE_TO_ORDER_AWAITING_FRAGMENT_KEY"
    private const val FIREBASE_AUTH_ID_TOKEN_KEY = "FIREBASE_AUTH_ID_TOKEN_KEY"
    private const val FIREBASE_MESSAGE_REG_TOKEN_KEY = "FIREBASE_MESSAGE_REG_TOKEN_KEY"
    private const val USER_EMAIL_KEY = "USER_EMAIL_KEY"
    const val EMPTY_STRING = "not initialized"

    private const val BASE_URL = "http://10.0.2.2:5000/api/"

    private val prefs: SharedPreferences by lazy {
        CoffeeDoseApplication.applicationContext().getSharedPreferences(PREFS_NAME,MODE_PRIVATE)
        /*Pref
        val act = (CoffeeDoseApplication.applicationContext() as CoffeeDoseApplication).currentActivity
        act!!.getSharedPreferences(PREFS_NAME,MODE_PRIVATE)*/
    }

    override fun saveBaseUrl(url: String) {
        putValue(BASE_URL_KEY to url)
    }

    override fun getBaseUrl() = prefs.getString(BASE_URL_KEY, BASE_URL)!!

    override fun saveIdToken(token: String) {
        putValue(FIREBASE_AUTH_ID_TOKEN_KEY to token)
    }

    override fun getIdToken() = prefs.getString(FIREBASE_AUTH_ID_TOKEN_KEY, EMPTY_STRING)!!

    override fun saveFcmRegToken(token: String) = putValue(FIREBASE_MESSAGE_REG_TOKEN_KEY to token)

    override fun getFcmRegToken() = prefs.getString(FIREBASE_MESSAGE_REG_TOKEN_KEY, EMPTY_STRING)!!

    /*fun getLastOrderId() = prefs.getInt(ORDER_ID_KEY,-1)

    fun saveLastOrderId(orderId:Int) = putValue(ORDER_ID_KEY to orderId)*/

    override fun getNavigateToOrderAwaitFrag() =
        prefs.getBoolean(NAVIGATE_TO_ORDER_AWAITING_FRAGMENT_KEY, false)

    override fun saveNavigateToOrderAwaitFrag(nav: Boolean) =
        putValue(NAVIGATE_TO_ORDER_AWAITING_FRAGMENT_KEY to nav)

    override fun saveAppTheme(theme: Int) = putValue(APP_THEME_KEY to theme)

    override fun getAppTheme(): Int = prefs.getInt(APP_THEME_KEY, AppCompatDelegate.MODE_NIGHT_NO)

    override fun saveUserEmail(email: String) {
        putValue(USER_EMAIL_KEY to email)
    }

    override fun getUserEmail() = prefs.getString(USER_EMAIL_KEY, UserA.DEFAULT_EMAIL)!!

    override fun getDeviceID(): String {
        var id = prefs.getString(DEVICE_ID, EMPTY_STRING)
        if (id == EMPTY_STRING) {
            id = UUID.randomUUID().toString()
            putValue(DEVICE_ID to id)

        }
        return id!!
    }


    private fun putValue(pair: Pair<String, Any>) = with(prefs.edit()) {
        val key = pair.first

        when (val value = pair.second) {
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