package xyz.tcreopargh.amttd_web.common.exception

import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import com.google.gson.stream.MalformedJsonException
import org.springframework.web.client.HttpStatusCodeException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.http.HttpConnectTimeoutException
import java.net.http.HttpTimeoutException
import java.sql.SQLException

/**
 * @author TCreopargh
 * A easily serializable exception class
 */
@Suppress("unused")
class AmttdException(val errorCode: ErrorCode, val nestedException: Exception? = null) :
    Exception("$errorCode${nestedException?.run { " Nested exception is: ${this.stackTraceToString()}" } ?: ""}") {
    enum class ErrorCode(val value: Int) {
        // Everything is OK, but why would you use it in an exception
        OK(0),

        // The error is unknown and we don't know why
        UNKNOWN(10000),
        EXCEPTION_NOT_AVAILABLE(10001),

        // IO Exceptions
        IO_EXCEPTION(20000),
        TIMED_OUT(20001),
        CONNECTION_REFUSED(20002),
        CONNECTION_CLOSED(20003),
        RESOURCE_NOT_FOUND(20004),
        SOCKET_ERROR(20005),
        UNKNOWN_HOST(20006),
        CONNECTION_ERROR(20007),
        DATABASE_ERROR(20008),

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
        ILLEGAL_ARGUMENTS(30007),

        // Server related
        SERVICE_ERROR(40000),

        REQUESTED_ENTITY_NOT_FOUND(40001),
        FAILED_TO_CREATE_ENTITY(40002),
        FAILED_TO_READ_ENTITY(40003),
        FAILED_TO_UPDATE_ENTITY(40004),
        FAILED_TO_DELETE_ENTITY(40005),
        ACTION_NOT_SUPPORTED(40006),

        // Authentication related
        AUTHENTICATION_ERROR(41000),
        LOGIN_FAILED(41001),
        REGISTER_FAILED(41002),
        FIELD_MISSING(41003),
        ILLEGAL_EMAIL(41004),
        ILLEGAL_USERNAME(41005),
        ILLEGAL_PASSWORD(41006),
        INVALID_TOKEN(41007),
        USER_NOT_FOUND(41008),
        INCORRECT_PASSWORD(41009),
        CORRUPTED_DATA(41010),
        USER_ALREADY_EXISTS(41011),
        LOGIN_REQUIRED(41012),

        // HTTP Status
        HTTP_ERROR(42000),
        MULTIPLE_CHOICES(42300),
        MOVED_PERMANENTLY(42301),
        FOUND(42302),
        BAD_REQUEST(42400),
        UNAUTHORIZED(42401),
        FORBIDDEN(42403),
        NOT_FOUND(42404),
        METHOD_NOT_ALLOWED(42405),
        NOT_ACCEPTABLE(42406),
        REQUEST_TIMEOUT(42408),
        PAYLOAD_TOO_LARGE(42413),
        REQUEST_URI_TOO_LONG(42414),

        // Why does this exist
        I_AM_A_TEAPOT(42418),

        INTERNAL_SERVER_ERROR(42500),
        NOT_IMPLEMENTED(42501),
        BAD_GATEWAY(42502),
        SERVICE_UNAVAILABLE(42503),
        GATEWAY_TIMEOUT(42504),
        HTTP_VERSION_NOT_SUPPORTED(42505),
        LOOP_DETECTED(42508);

        companion object {
            fun getFromErrorCode(errorCode: Int?): ErrorCode {
                if (errorCode == null) return EXCEPTION_NOT_AVAILABLE
                for (entry in values()) {
                    if (entry.value == errorCode) {
                        return entry
                    }
                }
                return UNKNOWN
            }

            fun getFromException(e: Exception?): ErrorCode = when (e) {
                null                               -> EXCEPTION_NOT_AVAILABLE
                is AmttdException                  -> e.errorCode
                is SocketTimeoutException          -> TIMED_OUT
                is JsonParseException              -> INVALID_JSON
                is JsonSyntaxException             -> INVALID_JSON
                is HttpTimeoutException            -> TIMED_OUT
                is HttpConnectTimeoutException     -> REQUEST_TIMEOUT
                is MalformedJsonException          -> MALFORMED_JSON
                is ClassCastException              -> CLASS_CAST_EXCEPTION
                is NullPointerException            -> NULL_POINTER_EXCEPTION
                is ConcurrentModificationException -> CONCURRENT_MODIFICATION
                is ArithmeticException             -> MATH_ERROR
                is IndexOutOfBoundsException       -> INDEX_OUT_OF_BOUNDS
                is SocketException                 -> SOCKET_ERROR
                is ConnectException                -> CONNECTION_ERROR
                is SQLException                    -> DATABASE_ERROR
                is HttpStatusCodeException         -> getFromErrorCode(e.statusCode.value() + HTTP_ERROR.value)
                is IOException                     -> IO_EXCEPTION
                is RuntimeException                -> RUNTIME_EXCEPTION
                else                               -> UNKNOWN
            }
        }
    }

    companion object {
        fun getFromErrorCode(errorCode: Int?): AmttdException {
            return AmttdException(ErrorCode.getFromErrorCode(errorCode))
        }

        fun getFromException(e: Exception?): AmttdException =
            AmttdException(ErrorCode.getFromException(e), e)
    }

    val errorCodeValue: Int
        get() = errorCode.value
}