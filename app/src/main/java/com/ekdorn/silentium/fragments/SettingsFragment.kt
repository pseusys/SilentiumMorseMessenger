package com.ekdorn.silentium.fragments

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.preference.*
import com.ekdorn.silentium.R
import com.ekdorn.silentium.activities.ProxyActivity
import com.ekdorn.silentium.core.BiBit
import com.ekdorn.silentium.core.Morse
import com.ekdorn.silentium.managers.NetworkManager
import com.ekdorn.silentium.managers.PreferenceManager
import com.ekdorn.silentium.managers.UserManager
import com.ekdorn.silentium.preferences.ConfirmationPreference
import com.ekdorn.silentium.preferences.ConfirmationPreferenceDialogFragment
import com.ekdorn.silentium.preferences.ImagePreference
import com.ekdorn.silentium.utils.observe
import kotlinx.coroutines.launch


class SettingsFragment : PreferenceFragmentCompat() {
    companion object {
        private const val DIALOG_FRAGMENT_TAG = "ConfirmationPreferenceDialog"

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

            findPreference<ConfirmationPreference>(resources.getString(R.string.pref_account_logout_key))!!.setConfirmationListener {
                if (it) lifecycleScope.launch {
                    NetworkManager.logout(requireActivity())
                    logout()
                }
            }

            findPreference<ConfirmationPreference>(resources.getString(R.string.pref_account_leave_key))!!.setConfirmationListener {
                if (it) lifecycleScope.launch {
                    NetworkManager.leave(requireActivity())
                    logout()
                }
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

    private fun logout() = requireActivity().let {
        it.startActivity(Intent(it, ProxyActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK))
        it.finish()
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

    @Suppress("deprecation")
    override fun onDisplayPreferenceDialog(preference: Preference) {
        if (preference is ConfirmationPreference) ConfirmationPreferenceDialogFragment.newInstance(preference).let {
            it.setTargetFragment(this, 0)
            it.show(this.parentFragmentManager, DIALOG_FRAGMENT_TAG)
        } else super.onDisplayPreferenceDialog(preference)
    }
}
