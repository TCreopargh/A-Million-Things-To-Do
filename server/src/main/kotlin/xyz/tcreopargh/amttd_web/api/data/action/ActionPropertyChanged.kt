package xyz.tcreopargh.amttd_web.api.data.action

import xyz.tcreopargh.amttd_web.api.data.UserImpl
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author TCreopargh
 */
class ActionTitleChanged(
    override val actionId: UUID,
    override val user: UserImpl?,
    // Changed title
    override val oldValue: String?,
    override val newValue: String?,
    override val timeCreated: Calendar
) : IAction {

    override val actionType: ActionType = ActionType.TITLE_CHANGED
}

class ActionDescriptionChanged(
    override val actionId: UUID,
    override val user: UserImpl?,
    // Changed description
    override val oldValue: String?,
    override val newValue: String?,
    override val timeCreated: Calendar
) : IAction {

    override val actionType: ActionType = ActionType.DESCRIPTION_CHANGED
}

class ActionDeadlineChanged(
    override val actionId: UUID,
    override val user: UserImpl?,
    override val timeCreated: Calendar,
    override var oldValue: String?,
    override var newValue: String?
) : IAction {

    companion object {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    }

    var newDeadline: Calendar?
        get() = try {
            newValue?.let { str ->
                dateFormat.parse(str)?.let {
                    Calendar.getInstance().apply { time = it }
                }
            }
        } catch (_: Exception) {
            null
        }
        set(value) = try {
            value?.time?.let {
                newValue = dateFormat.format(it)
            } ?: Unit
        } catch (e: Exception) {
        }

    var oldDeadline: Calendar?
        get() = try {
            oldValue?.let { str ->
                dateFormat.parse(str)?.let {
                    Calendar.getInstance().apply { time = it }
                }
            }
        } catch (_: Exception) {
            null
        }
        set(value) = try {
            value?.time?.let {
                oldValue = dateFormat.format(it)
            } ?: Unit
        } catch (e: Exception) {
        }

    override val actionType: ActionType = ActionType.DEADLINE_CHANGED
}