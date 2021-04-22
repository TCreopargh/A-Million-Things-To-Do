package xyz.tcreopargh.amttd

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import xyz.tcreopargh.amttd.data.login.LoginDataSource
import xyz.tcreopargh.amttd.data.login.LoginRepository
import xyz.tcreopargh.amttd.data.login.LoginResult
import xyz.tcreopargh.amttd.ui.login.AuthResult
import xyz.tcreopargh.amttd.ui.login.LoggedInUserView
import xyz.tcreopargh.amttd.user.LocalUser
import java.util.*

/**
 * @author TCreopargh
 */
class MainViewModel : ViewModel() {
    private var _loginRepo = MutableLiveData<LoginRepository>()
    var loginRepository: LiveData<LoginRepository> = _loginRepo

    init {
        _loginRepo.value = LoginRepository(LoginDataSource())
    }

    fun setUser(user: LocalUser?) {
        _loginRepo.value?.loggedInUser = user
    }

    fun getUser() = _loginRepo.value?.loggedInUser


}