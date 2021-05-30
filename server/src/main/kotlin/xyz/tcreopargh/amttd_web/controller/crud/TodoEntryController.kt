package xyz.tcreopargh.amttd_web.controller.crud

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import xyz.tcreopargh.amttd_web.annotation.LoginRequired
import xyz.tcreopargh.amttd_web.common.bean.request.TodoEntryCrudRequest
import xyz.tcreopargh.amttd_web.common.bean.response.TodoEntryCrudResponse
import xyz.tcreopargh.amttd_web.common.data.CrudType
import xyz.tcreopargh.amttd_web.common.data.TodoEntryImpl
import xyz.tcreopargh.amttd_web.common.exception.AmttdException
import xyz.tcreopargh.amttd_web.controller.ControllerBase
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
            if (body.operation != CrudType.CREATE) {
                verifyTodoEntry(request, body.entity?.entryId)
            }
            when (body.operation) {
                CrudType.READ   -> {
                    var entry: EntityTodoEntry? = null
                    body.entity?.entryId?.let {
                        entry = todoEntryService.findByIdOrNull(it)
                    } ?: throw AmttdException(AmttdException.ErrorCode.JSON_NON_NULLABLE_VALUE_IS_NULL)
                    val ret = entry?.let { TodoEntryImpl(it) }
                        ?: throw AmttdException(AmttdException.ErrorCode.REQUESTED_ENTITY_NOT_FOUND)
                    TodoEntryCrudResponse(operation = CrudType.READ, success = true, entity = ret)
                }
                CrudType.CREATE -> {
                    TODO("Add new to-do entry")
                }
                CrudType.DELETE -> {
                    TODO("Remove to-do entry")
                }
                else            -> TodoEntryCrudResponse(
                    success = false,
                    error = AmttdException.ErrorCode.ACTION_NOT_SUPPORTED.value
                )
            }
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