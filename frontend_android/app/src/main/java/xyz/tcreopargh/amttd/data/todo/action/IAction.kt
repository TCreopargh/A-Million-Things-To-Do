package xyz.tcreopargh.amttd.data.todo.action

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import xyz.tcreopargh.amttd.user.AbstractUser
import xyz.tcreopargh.amttd.util.setColor
import java.util.*

/**
 * @author TCreopargh
 * Actions are operations that are done to a TodoEntry.
 */
interface IAction: Comparable<IAction> {
    /**
     * The user who initiated the action
     */
    val user: AbstractUser
    val timeCreated: Calendar
    fun getUserNameText(): Spannable {
        return SpannableString(user.userName).setColor(Color.parseColor("#2196f3"))
    }

    fun getDisplayText(): Spannable {
        return getActionText()
    }

    fun getActionText(): Spannable

    override fun compareTo(other: IAction): Int = this.timeCreated.compareTo(other.timeCreated)
}
