package xyz.tcreopargh.amttd

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import xyz.tcreopargh.amttd.data.login.LoginDataSource
import xyz.tcreopargh.amttd.data.login.LoginRepository
import xyz.tcreopargh.amttd.data.user.LocalUser
import xyz.tcreopargh.amttd.ui.ViewModelBase

/**
 * @author TCreopargh
 *
 * Use [androidx.lifecycle.ViewModel]s to handle async operations and persistent data.
 */
class MainViewModel : ViewModelBase() {
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