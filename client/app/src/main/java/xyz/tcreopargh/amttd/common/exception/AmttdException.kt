package xyz.tcreopargh.amttd.common.exception

import android.content.Context
import androidx.annotation.StringRes
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import com.google.gson.stream.MalformedJsonException
import xyz.tcreopargh.amttd.R
import java.io.IOException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.sql.SQLException

/**
 * @author TCreopargh
 * A easily serializable exception class
 */
@Suppress("unused")
class AmttdException(val errorCode: ErrorCode, val nestedException: Exception? = null) :
    Exception("$errorCode${nestedException?.run { " Nested exception is: ${this.stackTraceToString()}" } ?: ""}") {
    enum class ErrorCode(val value: Int, @StringRes val localizedString: Int? = null) {
        // Everything is OK, but why would you use it in an exception
        OK(0, R.string.ok),

        // The error is unknown and we don't know why
        UNKNOWN(10000, R.string.unknown_error),
        EXCEPTION_NOT_AVAILABLE(10001, R.string.exception_not_available),

        // IO Exceptions
        IO_EXCEPTION(20000, R.string.io_exception),
        TIMED_OUT(20001, R.string.timed_out),
        CONNECTION_REFUSED(20002, R.string.connection_refused),
        CONNECTION_CLOSED(20003, R.string.connection_closed),
        RESOURCE_NOT_FOUND(20004, R.string.resource_not_found),
        SOCKET_ERROR(20005, R.string.socket_error),
        UNKNOWN_HOST(20006, R.string.unknown_host),
        CONNECTION_ERROR(20007, R.string.connection_error),
        DATABASE_ERROR(20008, R.string.database_error),

        // JSON related
        INVALID_JSON(21000, R.string.invalid_json),
        MALFORMED_JSON(21001, R.string.malformed_json),
        JSON_MISSING_FIELD(21002, R.string.json_missing_field),
        JSON_NON_NULLABLE_VALUE_IS_NULL(21003, R.string.json_non_nullable_value_is_null),

        // Runtime Exceptions
        RUNTIME_EXCEPTION(30000, R.string.runtime_exception),
        CLASS_CAST_EXCEPTION(30001, R.string.class_cast_exception),
        CONCURRENT_MODIFICATION(30002, R.string.concurrent_modification),
        MATH_ERROR(30003, R.string.math_error),
        INDEX_OUT_OF_BOUNDS(30004, R.string.index_out_of_bounds),
        NULL_POINTER_EXCEPTION(30005, R.string.null_pointer_exception),
        UNSUPPORTED_OPERATION(30006, R.string.unsupported_operation),
        ILLEGAL_ARGUMENTS(30007, R.string.illegal_arguments),

        // Server related
        SERVICE_ERROR(40000, R.string.service_error),

        REQUESTED_ENTITY_NOT_FOUND(40001, R.string.requested_entity_not_found),
        FAILED_TO_CREATE_ENTITY(40002, R.string.failed_to_create_entity),
        FAILED_TO_READ_ENTITY(40003, R.string.failed_to_read_entity),
        FAILED_TO_UPDATE_ENTITY(40004, R.string.failed_to_update_entity),
        FAILED_TO_DELETE_ENTITY(40005, R.string.failed_to_delete_entity),
        ACTION_NOT_SUPPORTED(40006, R.string.action_not_supported),

        // Authentication related
        AUTHENTICATION_ERROR(41000, R.string.authentication_error),
        LOGIN_FAILED(41001, R.string.login_failed),
        REGISTER_FAILED(41002, R.string.register_failed),
        FIELD_MISSING(41003, R.string.field_missing),
        ILLEGAL_EMAIL(41004, R.string.illegal_email),
        ILLEGAL_USERNAME(41005, R.string.illegal_username),
        ILLEGAL_PASSWORD(41006, R.string.illegal_password),
        INVALID_TOKEN(41007, R.string.invalid_token),
        USER_NOT_FOUND(41008, R.string.user_not_found),
        INCORRECT_PASSWORD(41009, R.string.incorrect_password),
        CORRUPTED_DATA(41010, R.string.corrupted_data),
        USER_ALREADY_EXISTS(41011, R.string.user_already_exists),
        LOGIN_REQUIRED(41012, R.string.login_required),
        UNAUTHORIZED_OPERATION(41013, R.string.unauthorized_operation),
        INSUFFICIENT_PERMISSION(41014, R.string.insufficient_permission),
        REQUESTED_ENTITY_INVALID(41015, R.string.requested_entity_invalid),
        UNIQUE_ID_CONFLICT(41016, R.string.unique_id_conflict),
        ALREADY_IN_WORKGROUP(41017, R.string.already_in_workgroup),

        // HTTP Status
        HTTP_ERROR(42000, R.string.http_error),
        MULTIPLE_CHOICES(42300, R.string.multiple_choices),
        MOVED_PERMANENTLY(42301, R.string.moved_permanently),
        FOUND(42302, R.string.found),
        BAD_REQUEST(42400, R.string.bad_request),
        UNAUTHORIZED(42401, R.string.unauthorized),
        FORBIDDEN(42403, R.string.forbidden),
        NOT_FOUND(42404, R.string.not_found),
        METHOD_NOT_ALLOWED(42405, R.string.method_not_allowed),
        NOT_ACCEPTABLE(42406, R.string.not_acceptable),
        REQUEST_TIMEOUT(42408, R.string.request_timeout),
        PAYLOAD_TOO_LARGE(42413, R.string.payload_too_large),
        REQUEST_URI_TOO_LONG(42414, R.string.request_uri_too_long),

        // Why does this exist
        I_AM_A_TEAPOT(42418, R.string.i_am_a_teapot),

        INTERNAL_SERVER_ERROR(42500, R.string.internal_server_error),
        NOT_IMPLEMENTED(42501, R.string.not_implemented),
        BAD_GATEWAY(42502, R.string.bad_gateway),
        SERVICE_UNAVAILABLE(42503, R.string.service_unavailable),
        GATEWAY_TIMEOUT(42504, R.string.gateway_timeout),
        HTTP_VERSION_NOT_SUPPORTED(42505, R.string.http_version_not_supported),
        LOOP_DETECTED(42508, R.string.loop_detected);

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
                is MalformedJsonException          -> MALFORMED_JSON
                is ClassCastException              -> CLASS_CAST_EXCEPTION
                is NullPointerException            -> NULL_POINTER_EXCEPTION
                is ConcurrentModificationException -> CONCURRENT_MODIFICATION
                is ArithmeticException             -> MATH_ERROR
                is IndexOutOfBoundsException       -> INDEX_OUT_OF_BOUNDS
                is SocketException                 -> SOCKET_ERROR
                is ConnectException                -> CONNECTION_ERROR
                is SQLException                    -> DATABASE_ERROR
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

    val localizedStringId: Int?
        get() = errorCode.localizedString

    fun getLocalizedString(context: Context?): String {
        if (context == null) return ""
        return context.getString(R.string.error_code_message,
            errorCode.value.toString(),
            errorCode.localizedString?.let { context.getString(it) } ?: errorCode.toString(),
            nestedException?.let { " " + nestedException.message } ?: ""
        )
    }

    val errorCodeValue: Int
        get() = errorCode.value
}