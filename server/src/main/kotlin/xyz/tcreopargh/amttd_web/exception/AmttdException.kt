package xyz.tcreopargh.amttd_web.exception

/**
 * @author TCreopargh
 * A easily serializable exception class
 */
@Suppress("unused")
class AmttdException(val errorCode: ErrorCode): Exception(errorCode.toString()) {
    enum class ErrorCode(val errorCode: Int) {
        // Everything is OK, but why would you use it in an exception
        OK(0),

        // The error is unknown and we don't know why
        UNKNOWN(10000),

        // IO Exceptions
        IO_EXCEPTION(20000),
        TIMED_OUT(20001),
        CONNECTION_REFUSED(20002),
        CONNECTION_CLOSED(20003),
        RESOURCE_NOT_FOUND(20004),
        SOCKET_ERROR(20005),
        UNKNOWN_HOST(20006),

        // JSON related
        INVALID_JSON(21000),
        MALFORMED_JSON(21001),
        JSON_MISSING_FIELD(21002),
        JSON_NON_NULLABLE_VALUE_IS_NULL(21003),

        // Runtime Exceptions
        RUNTIME_EXCEPTION(30000),
        CLASS_CAST_EXCEPTION(30001),
        CONCURRENT_MODIFICATION(30002),
        MATH_ERROR(30003),
        INDEX_OUT_OF_BOUNDS(30004),
        NULL_POINTER_EXCEPTION(30005),
        UNSUPPORTED_OPERATION(30006),

        // Authentication related
        AUTHENTICATION_ERROR(40000),
        LOGIN_FAILED(40001),
        REGISTER_FAILED(40002)

    }
}