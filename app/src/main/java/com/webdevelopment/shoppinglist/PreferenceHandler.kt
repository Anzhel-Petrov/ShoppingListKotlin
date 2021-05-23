package com.webdevelopment.shoppinglist

import android.content.Context
import androidx.preference.PreferenceManager

object PreferenceHandler {
    private const val SETTINGS_DARKMODE = "darkmode"

    fun useDarkMode(context: Context): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SETTINGS_DARKMODE, true)
    }
}