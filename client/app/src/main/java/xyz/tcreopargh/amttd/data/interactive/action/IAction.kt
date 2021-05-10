package xyz.tcreopargh.amttd.data.interactive.action

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import xyz.tcreopargh.amttd.data.interactive.ITask
import xyz.tcreopargh.amttd.data.interactive.IUser
import xyz.tcreopargh.amttd.data.interactive.TodoStatus
import xyz.tcreopargh.amttd.util.setColor
import java.util.*

/**
 * @author TCreopargh
 * Actions are operations that are done to a TodoEntry.
 */
interface IAction {
    /**
     * The user who initiated the action
     */
    val user: IUser
    val timeCreated: Calendar
    val actionType: ActionType

    /**
     * These properties might not exist
     */
    val stringExtra: String?
        get() = null
    val fromStatus: TodoStatus?
        get() = null
    val toStatus: TodoStatus?
        get() = null
    val task: ITask?
        get() = null

    fun getUserNameText(): Spannable {
        return SpannableString(user.username).setColor(Color.parseColor("#2196f3"))
    }

    fun getDisplayText(): Spannable {
        return getActionText()
    }

    fun getActionText(): Spannable
}
