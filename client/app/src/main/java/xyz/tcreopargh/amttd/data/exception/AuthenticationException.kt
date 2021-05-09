package xyz.tcreopargh.amttd.data.exception

/**
 * @author TCreopargh
 */
sealed class AuthenticationException(val state: State = State.UNKNOWN) :
    IllegalArgumentException("Authentication Failed! ${state.defaultMessage}") {
    enum class State(val defaultMessage: String) {
        ILLEGAL_USERNAME("Illegal Username"),
        ILLEGAL_PASSWORD("Illegal Password"),
        INVALID_TOKEN("Invalid Token"),
        USER_NOT_FOUND("User not found"),
        INCORRECT_PASSWORD("Password Incorrect"),
        CORRUPTED_DATA("Data is Corrupted"),
        USER_ALREADY_EXISTS("User with the same name already exists"),
        UNKNOWN("Unknown Error");
    }
}

class LoginFailedException(state: State = State.UNKNOWN) : AuthenticationException(state) {
    constructor(reason: String) : this(
        try {
            State.valueOf(reason)
        } catch (_: IllegalArgumentException) {
            State.UNKNOWN
        }
    )
}

class RegisterFailedException(state: State = State.UNKNOWN) : AuthenticationException(state) {
    constructor(reason: String) : this(
        try {
            State.valueOf(reason)
        } catch (_: IllegalArgumentException) {
            State.UNKNOWN
        }
    )
}

