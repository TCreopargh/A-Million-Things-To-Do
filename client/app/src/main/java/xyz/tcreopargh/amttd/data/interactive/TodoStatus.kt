package xyz.tcreopargh.amttd.data.interactive

import android.text.SpannableString
import androidx.annotation.ColorInt
import xyz.tcreopargh.amttd.R
import xyz.tcreopargh.amttd.util.i18n
import xyz.tcreopargh.amttd.util.setColor

/**
 * @author TCreopargh
 */
enum class TodoStatus(@ColorInt val color: Int, val sortOrder: Int) {
    NOT_STARTED(0xf44336, 2),
    IN_PLAN(0x3f51b5, 1),
    IN_PROGRESS(0x2196f3, 0),
    COMPLETED(0x8bc34a, -2),
    ON_HOLD(0x607d8b, -1),
    CANCELLED(0x9e9e9e, -3);

    fun isFinished() = this == COMPLETED || this == CANCELLED
    fun isActive() = !(this == ON_HOLD || this == CANCELLED || this == COMPLETED)
    fun getDisplayString() = when (this) {
        NOT_STARTED -> i18n(R.string.status_not_started)
        IN_PLAN     -> i18n(R.string.status_in_plan)
        IN_PROGRESS -> i18n(R.string.status_in_progress)
        COMPLETED   -> i18n(R.string.status_completed)
        ON_HOLD     -> i18n(R.string.status_on_hold)
        CANCELLED   -> i18n(R.string.status_cancelled)
    }

    fun getColoredString() = SpannableString(getDisplayString()).setColor(this.sortOrder)
}
