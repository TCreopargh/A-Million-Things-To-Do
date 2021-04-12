package xyz.tcreopargh.amttd.data.login

import android.util.Log
import xyz.tcreopargh.amttd.AMTTD
import xyz.tcreopargh.amttd.user.LocalUser
import java.util.*

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(val dataSource: LoginDataSource, var loggedInUser: LocalUser? = null) {

    companion object {
        // TODO: Remove this, this is a temporary uuid only used for testing
        val tempUUID: UUID = UUID.fromString("25759392-c457-4af6-b881-530f2fb31b64")
    }

    // in-memory cache of the loggedInUser object

    val isLoggedIn: Boolean
        get() = loggedInUser != null

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        loggedInUser = null
    }

    fun logout() {
        loggedInUser = null
        dataSource.logout()
    }

    fun login(username: String, password: String): LoginResult<LocalUser> {
        val result = dataSource.login(username, password)
        if (result is LoginResult.Success) {
            this.loggedInUser = result.data
        }
        Log.i(AMTTD.logTag, "Login status: $result")
        return result
    }

    fun loginWithAuthToken(uuid: UUID, authToken: String): LoginResult<LocalUser> {
        val result = dataSource.loginWithAuthToken(uuid, authToken)
        if (result is LoginResult.Success) {
            this.loggedInUser = result.data
        }
        return result
    }
}