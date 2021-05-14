package xyz.tcreopargh.amttd.ui.login

/**
 * @author TCreopargh
 * Authentication result : success (user details) or error message.
 */
data class AuthResult(
    val success: LoggedInUserView? = null,
    val errorCode: Int? = null,
    val isRegister: Boolean = false
)