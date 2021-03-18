package xyz.tcreopargh.amttd.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import xyz.tcreopargh.amttd.data.login.LoginRepository
import xyz.tcreopargh.amttd.data.login.LoginResult

import xyz.tcreopargh.amttd.R

/**
 * @author TCreopargh
 */
class LoginViewModel(val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<AuthResult>()
    val loginResult: LiveData<AuthResult> = _loginResult

    fun login(username: String, password: String) {

        // can be launched in a separate asynchronous job
        val result = loginRepository.login(username, password)

        if (result is LoginResult.Success) {
            _loginResult.value =
                AuthResult(success = LoggedInUserView(displayName = result.data.userName))
        } else {
            _loginResult.value = AuthResult(error = R.string.login_failed)
        }
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}