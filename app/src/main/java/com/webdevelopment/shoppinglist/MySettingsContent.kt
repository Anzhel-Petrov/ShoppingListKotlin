package com.webdevelopment.shoppinglist

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference

class MySettingsContent : PreferenceFragmentCompat(){
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
        val vibrateSwitch: SwitchPreference? =
            findPreference("darkmode")

        if (vibrateSwitch != null) {
            vibrateSwitch.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, newValue ->
                val switched: Boolean = (preference as SwitchPreference)
                    .isChecked
                if (!switched) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
                true
            }
        }
    }
}