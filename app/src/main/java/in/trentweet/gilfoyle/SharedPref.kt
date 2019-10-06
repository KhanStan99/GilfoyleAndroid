package `in`.trentweet.gilfoyle

import android.content.Context
import android.content.SharedPreferences

class SharedPref(context: Context) {

    private val preference = "PiedPiper"
    private val isLocationPermissionAllowed = "userBitCoinRate"

    private var preferences: SharedPreferences = context.getSharedPreferences(preference, 0)


    fun saveUserPrice(value: Float) {
        preferences.edit().putFloat(isLocationPermissionAllowed, value).apply()
    }

    fun getUserPrice(): Float { return preferences.getFloat(isLocationPermissionAllowed, 0.0F) }

}