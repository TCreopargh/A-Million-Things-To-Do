package xyz.tcreopargh.amttd.ui.settings

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.lifecycle.ViewModelProvider
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.reflect.TypeToken
import xyz.tcreopargh.amttd.AMTTD
import xyz.tcreopargh.amttd.MainActivity
import xyz.tcreopargh.amttd.R
import xyz.tcreopargh.amttd.common.bean.request.UserChangeAvatarRequest
import xyz.tcreopargh.amttd.common.bean.request.UserProfileChangeRequest
import xyz.tcreopargh.amttd.common.bean.response.SimpleResponse
import xyz.tcreopargh.amttd.common.exception.AmttdException
import xyz.tcreopargh.amttd.ui.login.LoginViewModel
import xyz.tcreopargh.amttd.util.*
import java.io.ByteArrayOutputStream
import java.util.*


class SettingsFragment : PreferenceFragmentCompat() {
    companion object {
        const val PREF_FILE_NAME = "app_settings"
        fun newInstance() = SettingsFragment()
    }

    val loggedInUser
        get() = (activity as? MainActivity)?.loggedInUser

    private var usernamePref: EditTextPreference? = null
    private var emailPref: EditTextPreference? = null
    private var passwordPref: EditTextPreference? = null
    private var nightModePref: ListPreference? = null
    private var languagePref: ListPreference? = null
    private var changeAvatarPref: Preference? = null

    private lateinit var viewModel: SettingsViewModel

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = PREF_FILE_NAME
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        viewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        usernamePref = preferenceScreen.findPreference("username")
        emailPref = preferenceScreen.findPreference("email")
        passwordPref = preferenceScreen.findPreference("password")
        nightModePref = preferenceScreen.findPreference("night_mode")
        languagePref = preferenceScreen.findPreference("language")
        changeAvatarPref = preferenceScreen.findPreference("profile_picture")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        viewModel.actualUsername.observe(viewLifecycleOwner) {
            usernamePref?.text = it
            if (it != loggedInUser?.username) {
                Toast.makeText(
                    context,
                    getString(R.string.username_changed, it),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        viewModel.actualEmail.observe(viewLifecycleOwner) {
            emailPref?.text = it
            if (it != loggedInUser?.email) {
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
        viewModel.isAvatarChanged.observe(viewLifecycleOwner) {
            if (it) {
                Toast.makeText(
                    context,
                    getString(R.string.avatar_changed),
                    Toast.LENGTH_LONG
                ).show()
                viewModel.isAvatarChanged.value = false
                (activity as? MainActivity)?.loadUserAvatar()
            }
        }

        viewModel.actualUsername.value = loggedInUser?.username
        viewModel.actualEmail.value = loggedInUser?.email

        passwordPref?.text = ""
        @Suppress("UsePropertyAccessSyntax", "UNUSED_ANONYMOUS_PARAMETER")
        usernamePref?.setOnPreferenceChangeListener OnPrefChanged@{ preference, newValue ->
            val newUsername = newValue.toString()
            if (!LoginViewModel.isUsernameValid(newUsername)) {
                Toast.makeText(context, R.string.invalid_username, Toast.LENGTH_SHORT).show()
                return@OnPrefChanged false
            }
            val uuid = loggedInUser?.uuid ?: return@OnPrefChanged false
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
            val uuid = loggedInUser?.uuid ?: return@OnPrefChanged false
            return@OnPrefChanged sendUserProfileChangeRequest(
                UserProfileChangeRequest(
                    userId = uuid,
                    newEmail = newEmail
                )
            )
        }
        @Suppress("UsePropertyAccessSyntax", "UNUSED_ANONYMOUS_PARAMETER")
        passwordPref?.setOnPreferenceChangeListener OnPrefChanged@{ preference, newValue ->

            if (!LoginViewModel.isPasswordValid(newValue.toString())) {
                Toast.makeText(context, R.string.invalid_password, Toast.LENGTH_SHORT)
                    .show()
                return@OnPrefChanged false
            }

            AlertDialog.Builder(context).apply {
                val confirmPasswordText =
                    EditText(context, null, 0, R.style.Widget_AppCompat_EditText).apply {
                        afterTextChanged {
                            if (newValue.toString() != it) {
                                error = context.getString(R.string.password_mismatch)
                            }
                        }
                        hint = getString(R.string.password_confirm)
                        setAutofillHints(getString(R.string.password_confirm))
                        inputType =
                            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        val params = LinearLayoutCompat.LayoutParams(
                            LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                            LinearLayoutCompat.LayoutParams.WRAP_CONTENT
                        )
                        params.setMargins(64, 8, 64, 8)
                        layoutParams = params
                    }

                val layout = LinearLayoutCompat(context).apply {
                    orientation = LinearLayoutCompat.VERTICAL
                    addView(confirmPasswordText)
                }
                setView(layout)
                setTitle(R.string.password_confirm)
                setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
                setPositiveButton(R.string.confirm) { dialog, _ ->
                    if (newValue.toString() != confirmPasswordText.text.toString()) {
                        Toast.makeText(context, R.string.password_mismatch, Toast.LENGTH_SHORT)
                            .show()
                        return@setPositiveButton
                    }
                    val newPassword = newValue.toString()
                    if (!LoginViewModel.isPasswordValid(newPassword)) {
                        Toast.makeText(context, R.string.invalid_password, Toast.LENGTH_SHORT)
                            .show()
                    }
                    val uuid = (activity as? MainActivity)?.loggedInUser?.uuid
                    sendUserProfileChangeRequest(
                        UserProfileChangeRequest(
                            userId = uuid,
                            newPassword = newPassword
                        )
                    )
                    dialog.dismiss()
                }
            }.create().show()
            return@OnPrefChanged false
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

        @Suppress("UsePropertyAccessSyntax", "UNUSED_ANONYMOUS_PARAMETER")
        nightModePref?.setOnPreferenceChangeListener OnPrefChanged@{ preference, newValue ->
            when (newValue.toString().toIntOrNull()) {
                1    -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                2    -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                else -> setNightModeAutomatically()
            }
            return@OnPrefChanged true
        }

        @Suppress("UsePropertyAccessSyntax", "UNUSED_ANONYMOUS_PARAMETER")
        languagePref?.setOnPreferenceChangeListener OnPrefChanged@{ preference, newValue ->
            var language = newValue.toString()
            if (language == "default") {
                language = Locale.getDefault().language
            }
            Toast.makeText(
                context,
                context?.let {
                    getLocalizedResources(
                        it,
                        Locale(language)
                    ).getString(R.string.language_changed)
                },
                Toast.LENGTH_LONG
            ).show()
            return@OnPrefChanged true
        }

        @Suppress("UsePropertyAccessSyntax", "UNUSED_ANONYMOUS_PARAMETER")
        changeAvatarPref?.setOnPreferenceClickListener OnPrefClick@{
            ImagePicker.with(this)
                .cropSquare()
                .compress(512)
                .maxResultSize(256, 256)
                .start()
            return@OnPrefClick true
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK       -> {
                //Image Uri will not be null for RESULT_OK
                val uri: Uri = data?.data!!
                val bitmap =
                    try {
                        uri.let {
                            if (Build.VERSION.SDK_INT < 28) {
                                @Suppress("DEPRECATION")
                                MediaStore.Images.Media.getBitmap(
                                    context?.contentResolver,
                                    uri
                                )
                            } else {
                                val source =
                                    ImageDecoder.createSource(context?.contentResolver!!, uri)
                                ImageDecoder.decodeBitmap(source)
                            }
                        }
                    } catch (e: Exception) {
                        @Suppress("ThrowableNotThrown")
                        Toast.makeText(
                            context,
                            AmttdException.getFromException(e).localizedMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                        null
                    }

                val stream = ByteArrayOutputStream()
                bitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val byteArray: ByteArray = stream.toByteArray()

                // Send request
                Thread {
                    try {
                        val httpRequest = okHttpRequest("/user/change-avatar")
                            .post(
                                UserChangeAvatarRequest(
                                    userId = loggedInUser?.uuid,
                                    img = byteArray
                                ).toJsonRequest()
                            )
                            .build()
                        val response = AMTTD.okHttpClient.newCall(httpRequest).execute()
                        val body = response.body?.string() ?: "{}"
                        if (enableJsonDebugging) {
                            Log.i(AMTTD.logTag, body)
                        }
                        val result: SimpleResponse =
                            gson.fromJson(
                                body,
                                object : TypeToken<SimpleResponse>() {}.type
                            )
                        if (result.success != true) {
                            throw AmttdException.getFromErrorCode(result.error)
                        }
                        viewModel.isAvatarChanged.postValue(true)
                    } catch (e: Exception) {
                        Log.e(AMTTD.logTag, e.stackTraceToString())
                        viewModel.exception.postValue(AmttdException.getFromException(e))
                    }
                }.start()

            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(context, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }
            else                     -> {
                Toast.makeText(context, R.string.avatar_change_cancelled, Toast.LENGTH_SHORT).show()
            }
        }
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