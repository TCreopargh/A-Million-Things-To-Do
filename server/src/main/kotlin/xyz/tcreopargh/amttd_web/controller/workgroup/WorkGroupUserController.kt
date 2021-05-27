package xyz.tcreopargh.amttd_web.controller.workgroup

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import xyz.tcreopargh.amttd_web.annotation.LoginRequired
import xyz.tcreopargh.amttd_web.common.bean.request.GroupUserViewRequest
import xyz.tcreopargh.amttd_web.common.bean.response.GroupUserViewResponse
import xyz.tcreopargh.amttd_web.common.data.UserImpl
import xyz.tcreopargh.amttd_web.common.exception.AmttdException
import xyz.tcreopargh.amttd_web.controller.ControllerBase
import xyz.tcreopargh.amttd_web.entity.EntityUser
import xyz.tcreopargh.amttd_web.util.logger
import java.util.stream.Collectors
import javax.servlet.http.HttpServletRequest

@RestController
@LoginRequired
@RequestMapping("/workgroup/users")
class WorkGroupUserController : ControllerBase() {

    @PostMapping(
        "",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getUserList(request: HttpServletRequest, @RequestBody body: GroupUserViewRequest): GroupUserViewResponse {
        return try {
            verifyWorkgroup(request, body.groupId)
            var users: Set<EntityUser> = setOf()

            body.groupId?.let {
                val workGroup = workGroupService.findByIdOrNull(it)
                    ?: throw AmttdException(AmttdException.ErrorCode.REQUESTED_ENTITY_NOT_FOUND)
                users = workGroup.users
            }
            val list = users.stream().map {
                UserImpl(it)
            }.collect(Collectors.toList())
            list.sortByDescending { it.username }
            GroupUserViewResponse(success = true, users = list)
        } catch (e: Exception) {
            logger.error("Exception in WorkGroupUserController: ", e)
            GroupUserViewResponse(success = false, error = AmttdException.ErrorCode.getFromException(e).value)
        }
    }
}