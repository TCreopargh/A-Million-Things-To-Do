package xyz.tcreopargh.amttd.ui.settings

import android.os.Bundle
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import xyz.tcreopargh.amttd.MainActivity
import xyz.tcreopargh.amttd.R

class SettingsFragment : PreferenceFragmentCompat() {
    companion object {
        fun newInstance() = SettingsFragment()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        val usernamePref: EditTextPreference? = preferenceScreen.findPreference("username")
        usernamePref?.text = ((activity as? MainActivity)?.loggedInUser?.username)
    }

    override fun onStart() {
        super.onStart()
        (activity as? MainActivity)?.onFragmentStart(this)
    }

    override fun onStop() {
        super.onStop()
        (activity as? MainActivity)?.onFragmentStop(this)
    }
}