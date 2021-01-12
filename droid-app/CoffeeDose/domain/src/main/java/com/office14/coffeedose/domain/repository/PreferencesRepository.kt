package com.office14.coffeedose.domain.repository

interface PreferencesRepository {
    fun getBaseUrl() : String
    fun saveBaseUrl(url: String)
    fun getIdToken():String
    fun saveIdToken(token: String)
    fun getFcmRegToken():String
    fun saveFcmRegToken(token: String)
    fun getNavigateToOrderAwaitFrag():Boolean
    fun saveNavigateToOrderAwaitFrag(nav: Boolean)
    fun getAppTheme(): Int
    fun saveAppTheme(theme: Int)
    fun getUserEmail():String
    fun saveUserEmail(email: String)
    fun getDeviceID(): String
}