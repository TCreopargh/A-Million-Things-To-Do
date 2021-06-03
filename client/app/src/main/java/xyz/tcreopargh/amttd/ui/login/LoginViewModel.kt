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
                            errorCode = result.errorCode,
                        )
                    )
            }
        }.start()
    }

    fun register(email: String, password: String, username: String) {
        Thread {
            when (val result = loginRepository.register(email, password, username)) {
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
                            errorCode = result.errorCode,
                            isRegister = true
                        )
                    )
            }
        }.start()
    }

    fun loginDataChanged(email: String, password: String) {
        if (!isEmailValid(email)) {
            _loginForm.value = LoginFormState(emailError = R.string.invalid_email)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    fun usernameChanged(email: String, password: String, username: String) {
        if (!isEmailValid(email)) {
            _loginForm.value = LoginFormState(emailError = R.string.invalid_email)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else if (!isUsernameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    companion object {
        fun isEmailValid(email: String?): Boolean {
            return email?.matches("^\\S+@\\S+\\.\\S+\$".toRegex()) == true && email.length < 80
        }

        fun isUsernameValid(username: String?): Boolean {
            return username?.matches(Regex("^([a-zA-Z0-9_ ]|[^\\x00-\\xff]){1,32}\$")) == true && username.trim() == username && username.length < 64
        }

        fun isPasswordValid(password: String?): Boolean {
            return password?.length in 6..128 && password?.matches(Regex("^[\\x00-\\xff]{6,128}\$")) == true
        }
    }
}