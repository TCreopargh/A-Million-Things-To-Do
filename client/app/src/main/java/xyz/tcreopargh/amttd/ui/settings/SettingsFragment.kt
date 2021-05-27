package xyz.tcreopargh.amttd.ui.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import com.google.gson.reflect.TypeToken
import xyz.tcreopargh.amttd.AMTTD
import xyz.tcreopargh.amttd.MainActivity
import xyz.tcreopargh.amttd.R
import xyz.tcreopargh.amttd.common.bean.request.UserProfileChangeRequest
import xyz.tcreopargh.amttd.common.bean.response.SimpleResponse
import xyz.tcreopargh.amttd.common.exception.AmttdException
import xyz.tcreopargh.amttd.ui.login.LoginViewModel
import xyz.tcreopargh.amttd.util.*

class SettingsFragment : PreferenceFragmentCompat() {
    companion object {
        fun newInstance() = SettingsFragment()
    }

    private var usernamePref: EditTextPreference? = null
    private var emailPref: EditTextPreference? = null
    private var passwordPref: EditTextPreference? = null

    private lateinit var viewModel: SettingsViewModel

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        viewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        usernamePref = preferenceScreen.findPreference("username")
        emailPref = preferenceScreen.findPreference("email")
        passwordPref = preferenceScreen.findPreference("password")

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        viewModel.actualUsername.observe(viewLifecycleOwner) {
            usernamePref?.text = it
            if (it != (activity as? MainActivity)?.loggedInUser?.username) {
                Toast.makeText(
                    context,
                    getString(R.string.username_changed, it),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        viewModel.actualEmail.observe(viewLifecycleOwner) {
            emailPref?.text = it
            if (it != (activity as? MainActivity)?.loggedInUser?.email) {
                Toast.makeText(
                    context,
                    getString(R.string.email_changed, it),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        viewModel.isPasswordChanged.observe(viewLifecycleOwner) {
            if (it) {
                Toast.makeText(
                    context,
                    getString(R.string.password_changed),
                    Toast.LENGTH_LONG
                ).show()
                viewModel.isPasswordChanged.value = false
            }
        }

        viewModel.actualUsername.value = (activity as? MainActivity)?.loggedInUser?.username
        viewModel.actualEmail.value = (activity as? MainActivity)?.loggedInUser?.email

        passwordPref?.text = ""
        @Suppress("UsePropertyAccessSyntax", "UNUSED_ANONYMOUS_PARAMETER")
        usernamePref?.setOnPreferenceChangeListener OnPrefChanged@{ preference, newValue ->
            val newUsername = newValue.toString()
            if (!LoginViewModel.isUsernameValid(newUsername)) {
                Toast.makeText(context, R.string.invalid_username, Toast.LENGTH_SHORT).show()
                return@OnPrefChanged false
            }
            val uuid = (activity as? MainActivity)?.loggedInUser?.uuid ?: return@OnPrefChanged false
            return@OnPrefChanged sendUserProfileChangeRequest(
                UserProfileChangeRequest(
                    userId = uuid,
                    newUsername = newUsername
                )
            )
        }
        @Suppress("UsePropertyAccessSyntax", "UNUSED_ANONYMOUS_PARAMETER")
        emailPref?.setOnPreferenceChangeListener OnPrefChanged@{ preference, newValue ->
            val newEmail = newValue.toString()
            if (!LoginViewModel.isEmailValid(newEmail)) {
                Toast.makeText(context, R.string.invalid_email, Toast.LENGTH_SHORT).show()
                return@OnPrefChanged false
            }
            val uuid = (activity as? MainActivity)?.loggedInUser?.uuid ?: return@OnPrefChanged false
            return@OnPrefChanged sendUserProfileChangeRequest(
                UserProfileChangeRequest(
                    userId = uuid,
                    newEmail = newEmail
                )
            )
        }
        @Suppress("UsePropertyAccessSyntax", "UNUSED_ANONYMOUS_PARAMETER")
        passwordPref?.setOnPreferenceChangeListener OnPrefChanged@{ preference, newValue ->
            val newPassword = newValue.toString()
            if (!LoginViewModel.isPasswordValid(newPassword)) {
                Toast.makeText(context, R.string.invalid_password, Toast.LENGTH_SHORT).show()
                return@OnPrefChanged false
            }
            val uuid = (activity as? MainActivity)?.loggedInUser?.uuid ?: return@OnPrefChanged false
            return@OnPrefChanged sendUserProfileChangeRequest(
                UserProfileChangeRequest(
                    userId = uuid,
                    newPassword = newPassword
                )
            )
        }

        viewModel.exception.observe(viewLifecycleOwner) {
            it?.run {
                Toast.makeText(
                    context,
                    getString(R.string.error_occurred) + it.getLocalizedString(context),
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.exception.value = null
            }
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        (activity as? MainActivity)?.onFragmentStart(this)
        activity?.setTitle(R.string.settings_title)
    }

    override fun onStop() {
        super.onStop()
        (activity as? MainActivity)?.onFragmentStop(this)
    }

    private fun sendUserProfileChangeRequest(request: UserProfileChangeRequest): Boolean {
        Thread {
            try {
                val httpRequest = okHttpRequest("/user/change-profile")
                    .post(
                        request.toJsonRequest()
                    )
                    .build()
                val response = AMTTD.okHttpClient.newCall(httpRequest).execute()
                val body = response.body?.string() ?: "{}"
                if (enableJsonDebugging) {
                    Log.i(AMTTD.logTag, body)
                }
                // Don't simplify this
                val result: SimpleResponse =
                    gson.fromJson(
                        body,
                        object : TypeToken<SimpleResponse>() {}.type
                    )
                if (result.success != true) {
                    throw AmttdException.getFromErrorCode(result.error)
                }
                request.newUsername?.let { viewModel.actualUsername.postValue(it) }
                request.newEmail?.let { viewModel.actualEmail.postValue(it) }
                request.newPassword?.let { viewModel.isPasswordChanged.postValue(true) }
            } catch (e: Exception) {
                Log.e(AMTTD.logTag, e.stackTraceToString())
                viewModel.exception.postValue(AmttdException.getFromException(e))
            }
        }.start()
        return true
    }
}