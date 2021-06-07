package xyz.tcreopargh.amttd_web.controller.crud

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import xyz.tcreopargh.amttd_web.annotation.LoginRequired
import xyz.tcreopargh.amttd_web.api.data.CrudType
import xyz.tcreopargh.amttd_web.api.data.TodoStatus
import xyz.tcreopargh.amttd_web.api.data.action.ActionDeadlineChanged
import xyz.tcreopargh.amttd_web.api.data.action.ActionGeneric
import xyz.tcreopargh.amttd_web.api.data.action.ActionType
import xyz.tcreopargh.amttd_web.api.exception.AmttdException
import xyz.tcreopargh.amttd_web.api.json.request.ActionCrudRequest
import xyz.tcreopargh.amttd_web.api.json.response.ActionCrudResponse
import xyz.tcreopargh.amttd_web.controller.ControllerBase
import xyz.tcreopargh.amttd_web.entity.EntityAction
import xyz.tcreopargh.amttd_web.entity.EntityTask
import xyz.tcreopargh.amttd_web.entity.EntityTodoEntry
import xyz.tcreopargh.amttd_web.util.logger
import java.util.*
import javax.servlet.http.HttpServletRequest

/**
 * @author TCreopargh
 *
 * Handle actions.
 *
 * Note that when an action is created, its associated operation must be performed.
 *
 * Example: client requested to `CREATE` an `ActionTaskCompleted`,
 * the task associated with this action must be marked as completed in the database.
 */
@RestController
@LoginRequired
class ActionController : ControllerBase() {
    @PostMapping(
        "/action",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun handleAction(request: HttpServletRequest, @RequestBody body: ActionCrudRequest): ActionCrudResponse {
        return try {
            val (entry, _, user) = verifyTodoEntry(request, body.entryId, body.userId)
            when (body.operation) {
                CrudType.CREATE -> {
                    val entity = body.entity
                    val action: EntityAction = when (entity?.actionType) {
                        ActionType.COMMENT             -> {
                            val comment = entity.stringExtra
                            EntityAction(
                                actionId = UUID.randomUUID(),
                                user = user,
                                timeCreated = Calendar.getInstance(),
                                actionType = ActionType.COMMENT,
                                stringExtra = comment,
                                parent = entry
                            )
                        }
                        ActionType.TASK_COMPLETED      -> {
                            val taskId = body.entity?.task?.taskId
                                ?: throw AmttdException(AmttdException.ErrorCode.REQUESTED_ENTITY_NOT_FOUND)
                            val task = taskService.findByIdOrNull(taskId)
                                ?: throw AmttdException(AmttdException.ErrorCode.REQUESTED_ENTITY_NOT_FOUND)
                            task.completed = true
                            taskService.saveImmediately(task)
                            updateTodoEntryState(entry)
                            EntityAction(
                                actionId = UUID.randomUUID(),
                                user = user,
                                timeCreated = Calendar.getInstance(),
                                actionType = ActionType.TASK_COMPLETED,
                                task = task,
                                parent = entry
                            )
                        }
                        ActionType.TASK_UNCOMPLETED    -> {
                            val taskId = body.entity?.task?.taskId
                                ?: throw AmttdException(AmttdException.ErrorCode.REQUESTED_ENTITY_NOT_FOUND)
                            val task = taskService.findByIdOrNull(taskId)
                                ?: throw AmttdException(AmttdException.ErrorCode.REQUESTED_ENTITY_NOT_FOUND)
                            task.completed = false
                            taskService.saveImmediately(task)
                            updateTodoEntryState(entry)
                            EntityAction(
                                actionId = UUID.randomUUID(),
                                user = user,
                                timeCreated = Calendar.getInstance(),
                                actionType = ActionType.TASK_UNCOMPLETED,
                                task = task,
                                parent = entry
                            )
                        }
                        ActionType.TASK_ADDED          -> {
                            val taskImpl = entity.task ?: throw AmttdException(AmttdException.ErrorCode.INVALID_JSON)
                            val task = EntityTask(
                                taskId = UUID.randomUUID(),
                                name = taskImpl.name,
                                completed = taskImpl.completed,
                                parent = entry
                            )
                            taskService.saveImmediately(task)
                            updateTodoEntryState(entry)
                            EntityAction(
                                actionId = UUID.randomUUID(),
                                user = user,
                                timeCreated = Calendar.getInstance(),
                                actionType = ActionType.TASK_ADDED,
                                task = task,
                                parent = entry
                            )
                        }
                        ActionType.TASK_REMOVED        -> {
                            val taskId = body.entity?.task?.taskId
                                ?: throw AmttdException(AmttdException.ErrorCode.REQUESTED_ENTITY_NOT_FOUND)
                            val task = taskService.findByIdOrNull(taskId)
                                ?: throw AmttdException(AmttdException.ErrorCode.REQUESTED_ENTITY_NOT_FOUND)
                            taskService.markAsRemoved(taskId)
                            updateTodoEntryState(entry)
                            EntityAction(
                                actionId = UUID.randomUUID(),
                                user = user,
                                timeCreated = Calendar.getInstance(),
                                actionType = ActionType.TASK_REMOVED,
                                task = task,
                                parent = entry
                            )
                        }
                        ActionType.TASK_EDITED         -> {
                            val taskId = body.entity?.task?.taskId
                                ?: throw AmttdException(AmttdException.ErrorCode.REQUESTED_ENTITY_NOT_FOUND)
                            val task = taskService.findByIdOrNull(taskId)
                                ?: throw AmttdException(AmttdException.ErrorCode.REQUESTED_ENTITY_NOT_FOUND)
                            val oldName = task.name
                            task.apply {
                                name = entity.task?.name
                                    ?: throw AmttdException(AmttdException.ErrorCode.JSON_NON_NULLABLE_VALUE_IS_NULL)
                                completed = entity.task.completed
                            }
                            taskService.saveImmediately(task)
                            EntityAction(
                                actionId = UUID.randomUUID(),
                                user = user,
                                timeCreated = Calendar.getInstance(),
                                actionType = ActionType.TASK_EDITED,
                                task = task,
                                parent = entry,
                                oldValue = oldName,
                                newValue = task.name
                            )
                        }
                        ActionType.TITLE_CHANGED       -> {
                            entry.title = entity.newValue?.replace("\n", " ")?.trim()
                                ?: throw AmttdException(AmttdException.ErrorCode.JSON_NON_NULLABLE_VALUE_IS_NULL)
                            todoEntryService.saveImmediately(entry)
                            EntityAction(
                                actionId = UUID.randomUUID(),
                                user = user,
                                timeCreated = Calendar.getInstance(),
                                actionType = ActionType.TITLE_CHANGED,
                                oldValue = entity.oldValue,
                                newValue = entity.newValue,
                                parent = entry
                            )
                        }
                        ActionType.DESCRIPTION_CHANGED -> {
                            entry.description = entity.newValue
                                ?: throw AmttdException(AmttdException.ErrorCode.JSON_NON_NULLABLE_VALUE_IS_NULL)
                            todoEntryService.saveImmediately(entry)
                            EntityAction(
                                actionId = UUID.randomUUID(),
                                user = user,
                                timeCreated = Calendar.getInstance(),
                                actionType = ActionType.DESCRIPTION_CHANGED,
                                oldValue = entity.oldValue,
                                newValue = entity.newValue,
                                parent = entry
                            )
                        }
                        ActionType.STATUS_CHANGED      -> {
                            entry.status = entity.toStatus
                                ?: throw AmttdException(AmttdException.ErrorCode.JSON_NON_NULLABLE_VALUE_IS_NULL)
                            todoEntryService.saveImmediately(entry)
                            EntityAction(
                                actionId = UUID.randomUUID(),
                                user = user,
                                timeCreated = Calendar.getInstance(),
                                actionType = ActionType.STATUS_CHANGED,
                                fromStatus = entity.fromStatus,
                                toStatus = entity.toStatus,
                                parent = entry
                            )
                        }
                        ActionType.DEADLINE_CHANGED    -> {
                            val entityCasted = entity.action as? ActionDeadlineChanged
                            entry.deadline = entityCasted?.newDeadline
                            todoEntryService.saveImmediately(entry)
                            EntityAction(
                                actionId = UUID.randomUUID(),
                                user = user,
                                timeCreated = Calendar.getInstance(),
                                actionType = ActionType.DEADLINE_CHANGED,
                                oldValue = entity.oldValue,
                                newValue = entity.newValue,
                                parent = entry
                            )
                        }
                        else                           -> throw AmttdException(AmttdException.ErrorCode.UNSUPPORTED_OPERATION)
                    }
                    actionService.saveImmediately(action)
                    ActionCrudResponse(
                        operation = body.operation,
                        success = true,
                        entity = ActionGeneric(action)
                    )
                }
                else            -> throw AmttdException(AmttdException.ErrorCode.UNSUPPORTED_OPERATION)
            }
        } catch (e: Exception) {
            logger.error("Error in action controller add comment: ", e)
            ActionCrudResponse(
                operation = body.operation,
                success = false,
                error = AmttdException.ErrorCode.getFromException(e).value
            )
        }
    }

    fun updateTodoEntryState(entry: EntityTodoEntry) {
        if (entry.tasks.none { !it.completed }) {
            entry.status = TodoStatus.COMPLETED
            todoEntryService.saveImmediately(entry)
        } else if (entry.tasks.size > 1
            && entry.tasks.filter { it.completed }.size in 1 until entry.tasks.size
        ) {
            entry.status = TodoStatus.IN_PROGRESS
            todoEntryService.saveImmediately(entry)
        } else if (entry.tasks.none { it.completed }) {
            entry.status = TodoStatus.NOT_STARTED
            todoEntryService.saveImmediately(entry)
        }
    }
}