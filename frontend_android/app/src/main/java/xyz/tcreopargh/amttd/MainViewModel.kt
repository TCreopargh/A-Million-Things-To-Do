package xyz.tcreopargh.amttd

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import xyz.tcreopargh.amttd.data.login.LoginDataSource
import xyz.tcreopargh.amttd.data.login.LoginRepository
import xyz.tcreopargh.amttd.user.LocalUser

/**
 * @author TCreopargh
 */
class MainViewModel: ViewModel() {
    private var _loginRepo = MutableLiveData<LoginRepository>()
    var loginRepo: LiveData<LoginRepository> = _loginRepo

    init {
        _loginRepo.value = LoginRepository(LoginDataSource())
    }

    fun setUser(user: LocalUser?) {
        _loginRepo.value?.loggedInUser = user
    }

    fun getUser() = _loginRepo.value?.loggedInUser
}