package xyz.tcreopargh.amttd_web.controller.view

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import xyz.tcreopargh.amttd_web.bean.request.WorkGroupViewRequest
import xyz.tcreopargh.amttd_web.bean.response.WorkGroupViewResponse
import xyz.tcreopargh.amttd_web.controller.ControllerBase
import xyz.tcreopargh.amttd_web.data.WorkGroupImpl
import xyz.tcreopargh.amttd_web.entity.EntityWorkGroup
import xyz.tcreopargh.amttd_web.exception.AmttdException
import xyz.tcreopargh.amttd_web.util.logger
import java.util.stream.Collectors
import javax.servlet.http.HttpServletRequest

@RestController
class WorkGroupPresenter : ControllerBase() {

    @PostMapping(
        "/workgroups",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun resolveWorkGroups(request: HttpServletRequest, @RequestBody body: WorkGroupViewRequest): WorkGroupViewResponse {

        return try {
            var workGroups: Set<EntityWorkGroup> = setOf()

            body.uuid?.let {
                val user = userService.findById(it)
                user?.joinedWorkGroups?.run Groups@{
                    workGroups = this@Groups
                }
            }
            val list = workGroups.stream().map {
                WorkGroupImpl(it)
            }.collect(Collectors.toList())
            WorkGroupViewResponse(success = true, workGroups = list)
        } catch (e: Exception) {
            logger.error(e.toString())
            WorkGroupViewResponse(success = false, error = AmttdException.ErrorCode.getFromException(e).value)
        }
    }
}