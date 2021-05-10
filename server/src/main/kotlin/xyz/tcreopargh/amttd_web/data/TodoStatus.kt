package xyz.tcreopargh.amttd_web.data

/**
 * @author TCreopargh
 */
enum class TodoStatus(val color: Int, val sortOrder: Int) {
    NOT_STARTED(0xf44336, 2),
    IN_PLAN(0x3f51b5, 1),
    IN_PROGRESS(0x2196f3, 0),
    COMPLETED(0x8bc34a, -2),
    ON_HOLD(0x607d8b, -1),
    CANCELLED(0x9e9e9e, -3);

    fun isFinished() = this == COMPLETED || this == CANCELLED
    fun isActive() = !(this == ON_HOLD || this == CANCELLED || this == COMPLETED)
}
