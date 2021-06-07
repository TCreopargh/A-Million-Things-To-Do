package xyz.tcreopargh.amttd_web.controller.crud

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import xyz.tcreopargh.amttd_web.annotation.LoginRequired
import xyz.tcreopargh.amttd_web.api.data.CrudType
import xyz.tcreopargh.amttd_web.api.data.TodoEntryImpl
import xyz.tcreopargh.amttd_web.api.exception.AmttdException
import xyz.tcreopargh.amttd_web.api.json.request.TodoEntryCrudRequest
import xyz.tcreopargh.amttd_web.api.json.response.TodoEntryCrudResponse
import xyz.tcreopargh.amttd_web.controller.ControllerBase
import xyz.tcreopargh.amttd_web.entity.EntityTask
import xyz.tcreopargh.amttd_web.entity.EntityTodoEntry
import xyz.tcreopargh.amttd_web.util.logger
import javax.servlet.http.HttpServletRequest

/**
 * @author TCreopargh
 *
 * To-do entry related stuff
 */
@RestController
@LoginRequired
class TodoEntryController : ControllerBase() {

    /**
     * Respond to add, edit, view and delete operations on a single to-do entry entity.
     *
     * [body] The request body. CrudType indicates which action to perform,
     * but a controller might not implement all 4 types of operations.
     *
     * [TodoEntryCrudRequest.entity] represents the entity associated with this action.
     * Only necessary properties of it may be used in the controller to decide how to create/update the entity.
     * If the operation is `VIEW` or `DELETE`, only the id should be used.
     *
     * Otherwise, the id should be ignored because server should decide what ID an entity should have, not the client
     *
     * In this case, the id should be generated again on the server.
     *
     * @return An response of the operation. [TodoEntryCrudResponse.success] indicates if the operation is successful.
     *
     * Serialize errors to error codes and pass them via [TodoEntryCrudResponse.error] if the operation failed.
     *
     * [TodoEntryCrudResponse.entity] indicates the new entity. If not needed, this can be set to null.
     */
    @PostMapping(
        "/todo-entry",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun resolveAction(
        request: HttpServletRequest,
        @RequestBody body: TodoEntryCrudRequest
    ): TodoEntryCrudResponse {
        return try {
            val todoEntryCrudResponse = if (body.operation == CrudType.CREATE) {
                val (workGroup, user) = verifyWorkgroup(request, body.workGroupId, body.userId)
                val entity = EntityTodoEntry(
                    creator = user,
                    title = body.entity?.title ?: throw AmttdException(AmttdException.ErrorCode.ILLEGAL_ARGUMENTS),
                    parent = workGroup
                )
                todoEntryService.saveImmediately(entity)
                val task = EntityTask(
                    name = body.entity?.title ?: throw AmttdException(AmttdException.ErrorCode.ILLEGAL_ARGUMENTS),
                    completed = false,
                    parent = entity
                )
                taskService.saveImmediately(task)
                TodoEntryCrudResponse(operation = CrudType.CREATE, success = true, entity = TodoEntryImpl(entity))
            } else {
                val (entry, _, _) = verifyTodoEntry(request, body.entity?.entryId, body.userId)
                when (body.operation) {
                    CrudType.READ   -> {
                        val ret = TodoEntryImpl(entry)
                        TodoEntryCrudResponse(operation = CrudType.READ, success = true, entity = ret)
                    }
                    CrudType.DELETE -> {
                        todoEntryService.delete(entry)
                        TodoEntryCrudResponse(operation = CrudType.DELETE, success = true, entity = null)
                    }
                    else            -> TodoEntryCrudResponse(
                        success = false,
                        error = AmttdException.ErrorCode.ACTION_NOT_SUPPORTED.value
                    )
                }
            }
            todoEntryCrudResponse
        } catch (e: Exception) {
            logger.error("Error in todo entry crud: ", e)
            TodoEntryCrudResponse(
                operation = body.operation,
                success = false,
                error = AmttdException.ErrorCode.getFromException(e).value
            )
        }
    }
}
