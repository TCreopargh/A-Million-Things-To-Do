package xyz.tcreopargh.amttd.data.todo.action

import android.text.Spannable
import android.text.SpannableString
import xyz.tcreopargh.amttd.data.interactive.IUser
import xyz.tcreopargh.amttd.data.todo.Status
import xyz.tcreopargh.amttd.util.plus
import java.util.*

/**
 * @author TCreopargh
 */
class ActionStatusChanged(
    override val user: IUser,
    override val timeCreated: Calendar,
    val statusFrom: Status,
    val statusTo: Status
) : IAction {
    override fun getActionText(): Spannable {
        return SpannableString("changed status from ") +
                statusFrom.getColoredString() +
                SpannableString(" to ") +
                statusTo.getColoredString()

    }
}