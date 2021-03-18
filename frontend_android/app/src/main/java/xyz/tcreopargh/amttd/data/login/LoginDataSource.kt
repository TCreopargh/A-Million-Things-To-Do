package xyz.tcreopargh.amttd.data.login

import xyz.tcreopargh.amttd.user.LocalUser
import java.util.*

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    fun login(username: String, password: String): LoginResult<LocalUser> {
        // TODO: acquire UUID and authToken from server
        return loginWithAuthToken(LoginRepository.tempUUID, "$username-$password")
    }

    fun loginWithAuthToken(uuid: UUID, authToken: String): LoginResult<LocalUser> {
        // TODO: handle login with auth token here
        val arr = authToken.split("-", limit = 2)
        val fakeUserName = arr[0]
        val fakeUser = LocalUser(fakeUserName, uuid, authToken)
        return LoginResult.Success(fakeUser)
    }

    fun logout() {
        // TODO: revoke authentication
    }
}