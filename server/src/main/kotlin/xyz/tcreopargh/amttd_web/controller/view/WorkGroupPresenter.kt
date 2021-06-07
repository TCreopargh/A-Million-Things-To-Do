package xyz.tcreopargh.amttd_web.controller.view

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import xyz.tcreopargh.amttd_web.annotation.LoginRequired
import xyz.tcreopargh.amttd_web.api.data.WorkGroupImpl
import xyz.tcreopargh.amttd_web.api.exception.AmttdException
import xyz.tcreopargh.amttd_web.api.json.request.WorkGroupViewRequest
import xyz.tcreopargh.amttd_web.api.json.response.WorkGroupViewResponse
import xyz.tcreopargh.amttd_web.controller.ControllerBase
import xyz.tcreopargh.amttd_web.entity.EntityWorkGroup
import xyz.tcreopargh.amttd_web.util.logger
import java.util.stream.Collectors
import javax.servlet.http.HttpServletRequest

/**
 * @author TCreopargh
 *
 * Present a list of workgroups associated with the user.
 */
@RestController
@LoginRequired
class WorkGroupPresenter : ControllerBase() {

    @PostMapping(
        "/workgroups",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun resolveWorkGroups(request: HttpServletRequest, @RequestBody body: WorkGroupViewRequest): WorkGroupViewResponse {
        return try {
            val user = verifyUser(request, body.uuid)
            var workGroups: Set<EntityWorkGroup> = setOf()

            body.uuid?.let {
                workGroups = user.joinedWorkGroups
            }
            val list = workGroups.stream().map {
                WorkGroupImpl(it)
            }.collect(Collectors.toList())
            list.sortByDescending { it.timeCreated }
            WorkGroupViewResponse(success = true, workGroups = list)
        } catch (e: Exception) {
            logger.error("Exception in WorkGroupPresenter: ", e)
            WorkGroupViewResponse(success = false, error = AmttdException.ErrorCode.getFromException(e).value)
        }
    }
}