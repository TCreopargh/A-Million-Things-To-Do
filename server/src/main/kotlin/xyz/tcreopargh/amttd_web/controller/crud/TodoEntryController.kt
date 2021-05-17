package xyz.tcreopargh.amttd_web.controller.crud

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import xyz.tcreopargh.amttd_web.common.bean.request.TodoEntryActionRequest
import xyz.tcreopargh.amttd_web.common.bean.response.TodoEntryActionResponse
import xyz.tcreopargh.amttd_web.common.data.CrudType
import xyz.tcreopargh.amttd_web.common.data.TodoEntryImpl
import xyz.tcreopargh.amttd_web.common.exception.AmttdException
import xyz.tcreopargh.amttd_web.controller.ControllerBase
import xyz.tcreopargh.amttd_web.entity.EntityTodoEntry
import xyz.tcreopargh.amttd_web.util.logger
import javax.servlet.http.HttpServletRequest

@RestController
class TodoEntryController : ControllerBase() {
    @PostMapping(
        "/todo-entry",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun resolveAction(
        request: HttpServletRequest,
        @RequestBody body: TodoEntryActionRequest
    ): TodoEntryActionResponse {
        return try {
            when (body.operation) {
                CrudType.READ -> {
                    var entry: EntityTodoEntry? = null
                    body.entity?.entryId?.let {
                        entry = todoEntryService.findByIdOrNull(it)
                    }
                    val ret = entry?.let { TodoEntryImpl(it) }
                        ?: throw AmttdException(AmttdException.ErrorCode.REQUESTED_ENTITY_NOT_FOUND)
                    TodoEntryActionResponse(operation = CrudType.READ, success = true, entity = ret)
                }
                // TODO: Implement other actions
                else          -> TodoEntryActionResponse(
                    success = false,
                    error = AmttdException.ErrorCode.ACTION_NOT_SUPPORTED.value
                )
            }
        } catch (e: Exception) {
            logger.error("Error in todo entry crud: ", e)
            TodoEntryActionResponse(
                operation = CrudType.READ,
                success = false,
                error = AmttdException.ErrorCode.getFromException(e).value
            )
        }
    }
}