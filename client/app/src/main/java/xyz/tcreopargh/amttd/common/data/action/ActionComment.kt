package xyz.tcreopargh.amttd.common.data.action

import android.text.Spannable
import android.text.SpannableString
import xyz.tcreopargh.amttd.R
import xyz.tcreopargh.amttd.common.data.UserImpl
import xyz.tcreopargh.amttd.util.getColor
import xyz.tcreopargh.amttd.util.i18n
import xyz.tcreopargh.amttd.util.plus
import xyz.tcreopargh.amttd.util.setColor
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

    override fun getActionText(): Spannable {
        return SpannableString(user?.username + " ")
            .setColor(getColor(R.color.primary)) +
                SpannableString(
                    i18n(
                        R.string.action_comment,
                        comment
                    )
                )
    }

    override val stringExtra: String
        get() = comment

    override val actionType: ActionType = ActionType.COMMENT

    override fun getImageRes(): Int = R.drawable.ic_baseline_comment_24
}