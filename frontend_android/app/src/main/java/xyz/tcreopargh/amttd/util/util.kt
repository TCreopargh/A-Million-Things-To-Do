/**
 * @author TCreopargh
 */
package xyz.tcreopargh.amttd.util

import android.text.*
import android.text.style.ForegroundColorSpan
import android.widget.EditText
import androidx.annotation.ColorInt
import xyz.tcreopargh.amttd.Application

const val PACKAGE_NAME = "xyz.tcreopargh.amttd"
const val PACKAGE_NAME_DOT = "$PACKAGE_NAME."

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}

operator fun Spannable.plus(other: Spannable): Spannable {
    return SpannableStringBuilder(this).append(other)
}

fun SpannableString.setColor(@ColorInt color: Int): SpannableString = apply {
    setSpan(
        ForegroundColorSpan(color),
        0,
        length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
}

fun i18n(resId: Int) = Application.context.getString(resId)
