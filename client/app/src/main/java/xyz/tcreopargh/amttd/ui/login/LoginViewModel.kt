package xyz.tcreopargh.amttd.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import xyz.tcreopargh.amttd.R
import xyz.tcreopargh.amttd.data.login.LoginRepository
import xyz.tcreopargh.amttd.data.login.LoginResult
import xyz.tcreopargh.amttd.ui.ViewModelBase

/**
 * @author TCreopargh
 */
class LoginViewModel(val loginRepository: LoginRepository) : ViewModelBase() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<AuthResult>()
    val loginResult: LiveData<AuthResult> = _loginResult

    fun login(username: String, password: String) {
        Thread {
            when (val result = loginRepository.login(username, password)) {
                is LoginResult.Success ->
                    _loginResult.postValue(
                        AuthResult(success = LoggedInUserView(displayName = result.data.username))
                    )
                is LoginResult.Error   ->
                    _loginResult.postValue(
                        AuthResult(
                            error = R.string.login_failed,
                            errorString = (result as? LoginResult.Error)?.exception?.message
                        )
                    )
            }
        }.start()
    }

    fun register(username: String, password: String) {
        Thread {
            when (val result = loginRepository.register(username, password)) {
                is LoginResult.Success ->
                    _loginResult.postValue(
                        AuthResult(
                            success = LoggedInUserView(displayName = result.data.username),
                            isRegister = true
                        )
                    )
                is LoginResult.Error   ->
                    _loginResult.postValue(
                        AuthResult(
                            error = R.string.register_failed,
                            errorString = (result as? LoginResult.Error)?.exception?.message,
                            isRegister = true
                        )
                    )
            }
        }.start()
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

    private fun isUserNameValid(username: String): Boolean {
        return username.matches(Regex("^([\\u4e00-\\u9fa5]{2,3})|([A-Za-z0-9_]{3,32})|([a-zA-Z0-9_\\u4e00-\\u9fa5]{3,32})\$"))
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length in 6..1024
    }
}