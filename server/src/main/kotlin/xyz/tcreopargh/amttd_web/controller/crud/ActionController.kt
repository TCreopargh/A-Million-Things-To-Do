package xyz.tcreopargh.amttd_web.controller.crud

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import xyz.tcreopargh.amttd_web.annotation.LoginRequired
import xyz.tcreopargh.amttd_web.common.bean.request.ActionCrudRequest
import xyz.tcreopargh.amttd_web.common.bean.response.SimpleResponse
import xyz.tcreopargh.amttd_web.common.data.CrudType
import xyz.tcreopargh.amttd_web.common.data.action.ActionType
import xyz.tcreopargh.amttd_web.common.exception.AmttdException
import xyz.tcreopargh.amttd_web.controller.ControllerBase
import xyz.tcreopargh.amttd_web.entity.EntityAction
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
    fun addComment(request: HttpServletRequest, @RequestBody body: ActionCrudRequest): SimpleResponse {
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
                    when (body.entity?.actionType) {
                        ActionType.COMMENT -> {
                            val comment = entity?.stringExtra
                            val action = EntityAction(
                                actionId = UUID.randomUUID(),
                                user = user,
                                timeCreated = Calendar.getInstance(),
                                actionType = ActionType.COMMENT,
                                stringExtra = comment,
                                parent = entry
                            )
                            actionService.save(action)
                            SimpleResponse(
                                success = true
                            )
                        }
                        else               -> TODO("Not Implemented")
                    }
                }
                else            -> TODO("Not Implemented")
            }
        } catch (e: Exception) {
            logger.error("Error in action controller add comment: ", e)
            SimpleResponse(
                success = false,
                error = AmttdException.ErrorCode.getFromException(e).value
            )
        }
    }
}