package xyz.tcreopargh.amttd.ui.login

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import xyz.tcreopargh.amttd.ActivityManager
import xyz.tcreopargh.amttd.BaseActivity
import xyz.tcreopargh.amttd.R
import xyz.tcreopargh.amttd.util.PACKAGE_NAME_DOT
import xyz.tcreopargh.amttd.util.afterTextChanged
import xyz.tcreopargh.amttd.util.i18n
import java.util.*

/**
 * @author TCreopargh
 */
class LoginActivity : BaseActivity() {

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val login = findViewById<Button>(R.id.login)
        val register = findViewById<Button>(R.id.register)
        val loading = findViewById<ProgressBar>(R.id.loading)

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid
            register.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error, loginResult.errorString)
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
        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            username.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }

            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                loginViewModel.login(username.text.toString(), password.text.toString())
            }
            register.setOnClickListener {
                loading.visibility = View.VISIBLE
                loginViewModel.register(username.text.toString(), password.text.toString())
            }
        }
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
        // TODO : initiate successful logged in experience
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int, errorMsg: String?) {
        AlertDialog.Builder(this)
            .setTitle(errorString)
            .setMessage(errorMsg ?: i18n(R.string.login_failed_unknown_error))
            .setPositiveButton(R.string.confirm, null)
            .show()
    }
}
