package com.ekdorn.silentium.fragments

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.navArgs
import androidx.preference.*
import com.ekdorn.silentium.R
import com.ekdorn.silentium.managers.UserManager
import com.ekdorn.silentium.utils.observe
import com.ekdorn.silentium.preferences.ImagePreference
import com.ekdorn.silentium.core.BiBit
import com.ekdorn.silentium.core.Morse
import com.ekdorn.silentium.managers.PreferenceManager
import com.ekdorn.silentium.preferences.ConfirmationPreference


class SettingsFragment : PreferenceFragmentCompat() {
    companion object {
        const val default = "DEFAULT"
        const val keyboard = "KEYBOARD"
    }

    private lateinit var transmissionSpeed: SeekBarPreference
    private lateinit var useFarnsworthSpeed: SwitchPreferenceCompat

    private lateinit var dahSpeed: SeekBarPreference
    private lateinit var gapSpeed: SeekBarPreference
    private lateinit var endSpeed: SeekBarPreference

    private val args: SettingsFragmentArgs by navArgs()
    private val observer = observe(ActivityResultContracts.GetContent::class)

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = PreferenceManager.PREFERENCE_NAME

        if (args.access == default) {
            addPreferencesFromResource(R.xml.prefs_account)
            addPreferencesFromResource(R.xml.prefs_notifications)

            val userPhoto = findPreference<ImagePreference>(resources.getString(R.string.pref_account_picture_key))!!
            val userName = findPreference<EditTextPreference>(resources.getString(R.string.pref_account_name_key))!!
            userPhoto.setupCallback(observer)
            UserManager.UserDataStore(requireContext()).let {
                userPhoto.preferenceDataStore = it
                userName.preferenceDataStore = it
            }


        }
        addPreferencesFromResource(R.xml.prefs_morse)
        addPreferencesFromResource(R.xml.prefs_keyboard)
        addPreferencesFromResource(R.xml.prefs_additional)

        dahSpeed = findPreference(resources.getString(R.string.pref_morse_dah_key))!!
        gapSpeed = findPreference(resources.getString(R.string.pref_morse_gap_key))!!
        endSpeed = findPreference(resources.getString(R.string.pref_morse_end_key))!!

        val useStandardTiming = findPreference<SwitchPreferenceCompat>(resources.getString(R.string.pref_morse_timing_key))!!
        link(!useStandardTiming.isChecked)
        useStandardTiming.setOnPreferenceChangeListener { _, newValue ->
            if (newValue as Boolean) updateSpeed()
            link(!newValue)
            true
        }

        transmissionSpeed = findPreference(resources.getString(R.string.pref_morse_speed_key))!!
        useFarnsworthSpeed = findPreference(resources.getString(R.string.pref_morse_farnsworth_key))!!

        transmissionSpeed.summary = resources.getString(R.string.pref_morse_speed_summary_dynamic, Morse.getLength(transmissionSpeed.value, useFarnsworthSpeed.isChecked, BiBit.DIT))
        transmissionSpeed.setOnPreferenceChangeListener { _, newValue ->
            updateSpeed(speed = newValue as Int)
            true
        }

        useFarnsworthSpeed.setOnPreferenceChangeListener { _, newValue ->
            updateSpeed(farnsworth = newValue as Boolean)
            true
        }
    }

    private fun updateSpeed(speed: Int = transmissionSpeed.value, farnsworth: Boolean = useFarnsworthSpeed.isChecked) {
        val unit = Morse.getLength(speed, farnsworth, BiBit.DIT)
        transmissionSpeed.summary = resources.getString(R.string.pref_morse_speed_summary_dynamic, unit)
        dahSpeed.value = Morse.getLength(speed, farnsworth, BiBit.DAH)
        gapSpeed.value = Morse.getLength(speed, farnsworth, BiBit.GAP)
        endSpeed.value = Morse.getLength(speed, farnsworth, BiBit.END)
    }

    private fun link(hide: Boolean) {
        dahSpeed.isVisible = hide
        gapSpeed.isVisible = hide
        endSpeed.isVisible = hide
    }

    override fun onDisplayPreferenceDialog(preference: Preference) {
        if (preference is ConfirmationPreference) {
            // TODO: custom fragment
        } else super.onDisplayPreferenceDialog(preference)
    }
}
