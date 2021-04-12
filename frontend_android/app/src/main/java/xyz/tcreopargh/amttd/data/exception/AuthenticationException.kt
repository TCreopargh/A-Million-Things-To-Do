package xyz.tcreopargh.amttd.data.exception

/**
 * @author TCreopargh
 */
sealed class AuthenticationException(msg: String) : IllegalArgumentException(msg) {

}

class LoginFailedException(msg: String) : AuthenticationException(msg) {

}

class RegisterFailedException(msg: String) : AuthenticationException(msg) {

}
