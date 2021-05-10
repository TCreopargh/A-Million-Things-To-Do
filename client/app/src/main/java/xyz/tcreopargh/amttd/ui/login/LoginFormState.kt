package xyz.tcreopargh.amttd.ui.login

/**
 * @author TCreopargh
 * Data validation state of the login form.
 */
data class LoginFormState(
    val usernameError: Int? = null,
    val passwordError: Int? = null,
    val isDataValid: Boolean = false
)