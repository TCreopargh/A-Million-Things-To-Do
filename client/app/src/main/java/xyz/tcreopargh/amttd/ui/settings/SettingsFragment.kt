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
import xyz.tcreopargh.amttd.common.bean.request.ChangeUsernameRequest
import xyz.tcreopargh.amttd.common.bean.response.SimpleResponse
import xyz.tcreopargh.amttd.common.exception.AmttdException
import xyz.tcreopargh.amttd.util.*

class SettingsFragment : PreferenceFragmentCompat() {
    companion object {
        fun newInstance() = SettingsFragment()
    }

    private var usernamePref: EditTextPreference? = null

    private lateinit var viewModel: SettingsViewModel

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        viewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        usernamePref = preferenceScreen.findPreference("username")

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
        viewModel.actualUsername.value = (activity as? MainActivity)?.loggedInUser?.username
        @Suppress("UsePropertyAccessSyntax", "UNUSED_ANONYMOUS_PARAMETER")
        usernamePref?.setOnPreferenceChangeListener OnPrefChange@{ preference, newValue ->
            val newUsername = newValue.toString()
            val uuid = (activity as? MainActivity)?.loggedInUser?.uuid
            if (uuid == null) {
                viewModel.exception.postValue(AmttdException(AmttdException.ErrorCode.LOGIN_REQUIRED))
                return@OnPrefChange false
            }
            Thread {
                try {
                    val httpRequest = okhttp3.Request.Builder()
                        .post(
                            ChangeUsernameRequest(
                                userId = uuid,
                                newUsername = newUsername
                            ).toJsonRequest()
                        ).url(
                            rootUrl.withPath("/user/rename")
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
                    viewModel.actualUsername.postValue(newUsername)
                } catch (e: Exception) {
                    Log.e(AMTTD.logTag, e.stackTraceToString())
                    viewModel.exception.postValue(AmttdException.getFromException(e))
                }
            }.start()
            true
        }
        return view
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