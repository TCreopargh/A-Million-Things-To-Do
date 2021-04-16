package xyz.tcreopargh.amttd.data.todo

import android.graphics.Color
import android.text.SpannableString
import androidx.annotation.ColorInt
import xyz.tcreopargh.amttd.R
import xyz.tcreopargh.amttd.util.i18n
import xyz.tcreopargh.amttd.util.setColor

/**
 * @author TCreopargh
 */
enum class Status(@ColorInt val color: Int, val sortOrder: Int) {
    NOT_STARTED(Color.parseColor("#f44336"), 2),
    IN_PLAN(Color.parseColor("#3f51b5"), 1),
    IN_PROGRESS(Color.parseColor("#2196f3"), 0),
    COMPLETED(Color.parseColor("#8bc34a"), -2),
    ON_HOLD(Color.parseColor("#607d8b"), -1),
    CANCELLED(Color.parseColor("#9e9e9e"), -3);

    fun isFinished() = this == COMPLETED || this == CANCELLED
    fun isActive() = !(this == ON_HOLD || this == CANCELLED || this == COMPLETED)
    fun getDisplayString() = when (this) {
        NOT_STARTED -> i18n(R.string.status_not_started)
        IN_PLAN -> i18n(R.string.status_in_plan)
        IN_PROGRESS -> i18n(R.string.status_in_progress)
        COMPLETED -> i18n(R.string.status_completed)
        ON_HOLD -> i18n(R.string.status_on_hold)
        CANCELLED -> i18n(R.string.status_cancelled)
    }

    fun getColoredString() = SpannableString(getDisplayString()).setColor(this.sortOrder)
}
