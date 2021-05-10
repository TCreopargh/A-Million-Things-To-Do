package xyz.tcreopargh.amttd.data.interactive.action

import android.text.Spannable
import android.text.SpannableString
import xyz.tcreopargh.amttd.R
import xyz.tcreopargh.amttd.data.interactive.UserImpl
import xyz.tcreopargh.amttd.util.i18n
import java.util.*

/**
 * @author TCreopargh
 */
class ActionComment(
    override val user: UserImpl,
    override val timeCreated: Calendar,
    var comment: String
) : IAction {

    constructor(action: IAction) : this(
        user = UserImpl(action.user),
        timeCreated = action.timeCreated,
        comment = action.stringExtra ?: "",
    )

    override fun getActionText(): Spannable {
        return SpannableString(i18n(R.string.action_comment) + stringExtra)
    }

    override val stringExtra: String
        get() = comment

    override val actionType: ActionType = ActionType.COMMENT
}