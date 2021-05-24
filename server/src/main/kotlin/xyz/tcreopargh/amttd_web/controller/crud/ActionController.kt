package xyz.tcreopargh.amttd_web.controller.crud

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import xyz.tcreopargh.amttd_web.annotation.LoginRequired
import xyz.tcreopargh.amttd_web.common.bean.request.ActionCrudRequest
import xyz.tcreopargh.amttd_web.common.bean.response.ActionCrudResponse
import xyz.tcreopargh.amttd_web.common.data.CrudType
import xyz.tcreopargh.amttd_web.common.data.action.ActionGeneric
import xyz.tcreopargh.amttd_web.common.data.action.ActionType
import xyz.tcreopargh.amttd_web.common.exception.AmttdException
import xyz.tcreopargh.amttd_web.controller.ControllerBase
import xyz.tcreopargh.amttd_web.entity.EntityAction
import xyz.tcreopargh.amttd_web.entity.EntityTask
import xyz.tcreopargh.amttd_web.util.logger
import java.util.*
import javax.servlet.http.HttpServletRequest

@RestController
@LoginRequired
class ActionController : ControllerBase() {
    @PostMapping(
        "/action",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun addComment(request: HttpServletRequest, @RequestBody body: ActionCrudRequest): ActionCrudResponse {
        return try {
            when (body.operation) {
                CrudType.CREATE -> {
                    verifyTodoEntry(request, body.entryId)
                    verifyUser(request, body.userId)
                    val entry = todoEntryService.findByIdOrNull(
                        body.entryId ?: throw AmttdException(AmttdException.ErrorCode.REQUESTED_ENTITY_NOT_FOUND)
                    )
                    val user =
                        userService.findByIdOrNull(
                            body.userId ?: throw AmttdException(AmttdException.ErrorCode.INVALID_JSON)
                        )
                    val entity = body.entity
                    val action: EntityAction = when (body.entity?.actionType) {
                        ActionType.COMMENT          -> {
                            val comment = entity?.stringExtra
                            EntityAction(
                                actionId = UUID.randomUUID(),
                                user = user,
                                timeCreated = Calendar.getInstance(),
                                actionType = ActionType.COMMENT,
                                stringExtra = comment,
                                parent = entry
                            )
                        }
                        ActionType.TASK_COMPLETED   -> {
                            val taskId = body.entity?.task?.taskId
                                ?: throw AmttdException(AmttdException.ErrorCode.REQUESTED_ENTITY_NOT_FOUND)
                            val task = taskService.findByIdOrNull(taskId)
                                ?: throw AmttdException(AmttdException.ErrorCode.REQUESTED_ENTITY_NOT_FOUND)
                            task.completed = true
                            taskService.saveImmediately(task)
                            EntityAction(
                                actionId = UUID.randomUUID(),
                                user = user,
                                timeCreated = Calendar.getInstance(),
                                actionType = ActionType.TASK_COMPLETED,
                                task = task,
                                parent = entry
                            )
                        }
                        ActionType.TASK_UNCOMPLETED -> {
                            val taskId = body.entity?.task?.taskId
                                ?: throw AmttdException(AmttdException.ErrorCode.REQUESTED_ENTITY_NOT_FOUND)
                            val task = taskService.findByIdOrNull(taskId)
                                ?: throw AmttdException(AmttdException.ErrorCode.REQUESTED_ENTITY_NOT_FOUND)
                            task.completed = false
                            taskService.saveImmediately(task)
                            EntityAction(
                                actionId = UUID.randomUUID(),
                                user = user,
                                timeCreated = Calendar.getInstance(),
                                actionType = ActionType.TASK_UNCOMPLETED,
                                task = task,
                                parent = entry
                            )
                        }
                        ActionType.TASK_ADDED       -> {
                            val taskImpl = entity?.task ?: throw AmttdException(AmttdException.ErrorCode.INVALID_JSON)
                            val task = EntityTask(
                                taskId = UUID.randomUUID(),
                                name = taskImpl.name,
                                completed = taskImpl.completed,
                                parent = entry
                            )
                            taskService.saveImmediately(task)
                            EntityAction(
                                actionId = UUID.randomUUID(),
                                user = user,
                                timeCreated = Calendar.getInstance(),
                                actionType = ActionType.TASK_ADDED,
                                task = task,
                                parent = entry
                            )
                        }
                        ActionType.TASK_REMOVED     -> {
                            val taskId = body.entity?.task?.taskId
                                ?: throw AmttdException(AmttdException.ErrorCode.REQUESTED_ENTITY_NOT_FOUND)
                            val task = taskService.findByIdOrNull(taskId)
                                ?: throw AmttdException(AmttdException.ErrorCode.REQUESTED_ENTITY_NOT_FOUND)
                            taskService.markAsRemoved(taskId)
                            EntityAction(
                                actionId = UUID.randomUUID(),
                                user = user,
                                timeCreated = Calendar.getInstance(),
                                actionType = ActionType.TASK_REMOVED,
                                task = task,
                                parent = entry
                            )
                        }
                        ActionType.TASK_EDITED      -> {
                            val taskId = body.entity?.task?.taskId
                                ?: throw AmttdException(AmttdException.ErrorCode.REQUESTED_ENTITY_NOT_FOUND)
                            val task = taskService.findByIdOrNull(taskId)
                                ?: throw AmttdException(AmttdException.ErrorCode.REQUESTED_ENTITY_NOT_FOUND)
                            task.apply {
                                name = entity?.task?.name
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
                                parent = entry
                            )
                        }
                        else                        -> throw AmttdException(AmttdException.ErrorCode.UNSUPPORTED_OPERATION)
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
}