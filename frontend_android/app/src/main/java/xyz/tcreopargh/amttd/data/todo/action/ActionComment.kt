package xyz.tcreopargh.amttd.data.todo.action

import android.text.Spannable
import android.text.SpannableString
import xyz.tcreopargh.amttd.R
import xyz.tcreopargh.amttd.user.AbstractUser
import xyz.tcreopargh.amttd.util.i18n
import java.util.*

/**
 * @author TCreopargh
 */
class ActionComment(override val user: AbstractUser, override val timeCreated: Calendar, var comment: String) : IAction {
    override fun getActionText(): Spannable {
        return SpannableString(i18n(R.string.action_comment) + comment)
    }
}