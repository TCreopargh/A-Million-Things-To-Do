package xyz.tcreopargh.amttd.ui.login

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.SpannableStringBuilder
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.lifecycle.ViewModelProvider
import xyz.tcreopargh.amttd.ActivityManager
import xyz.tcreopargh.amttd.BaseActivity
import xyz.tcreopargh.amttd.R
import xyz.tcreopargh.amttd.common.exception.AmttdException
import xyz.tcreopargh.amttd.util.*
import java.util.*


/**
 * @author TCreopargh
 */
class LoginActivity : BaseActivity() {

    private lateinit var loginViewModel: LoginViewModel

    private var usernameText: EditText? = null

    private lateinit var loading: ProgressBar
    private lateinit var email: EditText
    private lateinit var password: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        email = findViewById<EditText>(R.id.email)
        password = findViewById<EditText>(R.id.password)
        loading = findViewById<ProgressBar>(R.id.loading)

        val login = findViewById<Button>(R.id.login)
        val register = findViewById<Button>(R.id.register)

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this@LoginActivity) Observer@{
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid
            register.isEnabled = loginState.isDataValid

            if (loginState.emailError != null) {
                email.error = getString(loginState.emailError)
            }
            if (loginState.usernameError != null) {
                usernameText?.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        }

        loginViewModel.loginResult.observe(this@LoginActivity) Observer@{
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.errorCode != null) {
                showLoginFailed(loginResult.errorCode, loginResult.isRegister)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
                setResult(Activity.RESULT_OK)

                // Send user info back to MainActivity
                // Complete and destroy login activity once successful
                val backIntent = Intent().putExtra(
                    PACKAGE_NAME_DOT + "User",
                    loginViewModel.loginRepository.loggedInUser
                )
                setResult(RESULT_OK, backIntent)
                finish()
            }
        }

        loginViewModel.exception.observe(this@LoginActivity) {
            it?.run {
                Toast.makeText(
                    this@LoginActivity,
                    getString(R.string.error_occurred) + it.getLocalizedString(this@LoginActivity),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        email.afterTextChanged {
            loginViewModel.loginDataChanged(
                email.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    email.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            email.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }

            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                loginViewModel.login(email.text.toString(), password.text.toString())
            }
            register.setOnClickListener {
                showSetUsernameDialog()
            }
        }
    }

    private fun showSetUsernameDialog() {
        AlertDialog.Builder(this@LoginActivity).apply {
            usernameText =
                EditText(this@LoginActivity, null, 0, R.style.Widget_AppCompat_EditText).apply {
                    afterTextChanged {
                        loginViewModel.usernameChanged(
                            email.text.toString(),
                            password.text.toString(),
                            it
                        )
                    }
                    hint = getString(R.string.prompt_username)
                    setAutofillHints(getString(R.string.prompt_username))
                    inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME
                    text = SpannableStringBuilder(
                        (
                                email.text.toString().split("@").getOrNull(0)
                                    ?: random.nextString(
                                        random.nextInt(
                                            8,
                                            15
                                        )
                                    )).replace("[^a-zA-Z0-9_]".toRegex(), " ")
                    )
                    val params = LinearLayoutCompat.LayoutParams(
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        LinearLayoutCompat.LayoutParams.WRAP_CONTENT
                    )
                    params.setMargins(32, 8, 32, 8)
                    layoutParams = params
                }
            val layout = LinearLayoutCompat(this@LoginActivity).apply {
                orientation = LinearLayoutCompat.VERTICAL
                addView(usernameText)
            }
            setView(layout)
            setTitle(R.string.set_username)
            setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
            setPositiveButton(R.string.action_register) { dialog, _ ->
                loading.visibility = View.VISIBLE
                loginViewModel.register(
                    email.text.toString(),
                    password.text.toString(),
                    usernameText?.text.toString()
                )
                dialog.cancel()
            }
            setOnDismissListener {
                loginViewModel.loginDataChanged(
                    email.text.toString(),
                    password.text.toString()
                )
                usernameText = null
            }
        }.create().show()
    }

    private var lastBackPressed: Long = 0

    override fun onBackPressed() {
        if (System.currentTimeMillis() - lastBackPressed < 2000) {
            ActivityManager.finishAll()
        } else {
            lastBackPressed = System.currentTimeMillis()
            Toast.makeText(this, R.string.press_back_again, Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(errorCode: Int, isRegister: Boolean) {
        AlertDialog.Builder(this)
            .setTitle(if (isRegister) R.string.register_failed else R.string.login_failed)
            .setMessage(AmttdException.getFromErrorCode(errorCode).getLocalizedString(this))
            .setPositiveButton(R.string.confirm, null)
            .show()
    }
}
