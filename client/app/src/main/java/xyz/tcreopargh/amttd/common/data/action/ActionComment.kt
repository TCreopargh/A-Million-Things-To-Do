package xyz.tcreopargh.amttd.common.data.action

import android.text.Spannable
import android.text.SpannableString
import xyz.tcreopargh.amttd.R
import xyz.tcreopargh.amttd.common.data.UserImpl
import xyz.tcreopargh.amttd.util.i18n
import java.util.*

/**
 * @author TCreopargh
 */
class ActionComment(
    override val actionId: UUID,
    override val user: UserImpl?,
    override val timeCreated: Calendar,
    var comment: String
) : IAction {

    constructor(action: IAction) : this(
        actionId = action.actionId,
        user = action.user?.let { UserImpl(it) },
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