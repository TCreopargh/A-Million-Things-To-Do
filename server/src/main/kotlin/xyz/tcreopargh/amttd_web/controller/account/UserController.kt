package xyz.tcreopargh.amttd_web.controller.account

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import xyz.tcreopargh.amttd_web.common.bean.request.ChangeUsernameRequest
import xyz.tcreopargh.amttd_web.common.bean.response.SimpleResponse
import xyz.tcreopargh.amttd_web.common.exception.AmttdException
import xyz.tcreopargh.amttd_web.controller.ControllerBase
import javax.servlet.http.HttpServletRequest

@RestController
class UserController : ControllerBase() {
    @PostMapping(
        "/user/rename",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun changeUsername(request: HttpServletRequest, @RequestBody body: ChangeUsernameRequest): SimpleResponse {
        val newUsername = body.newUsername
        val uuid = body.userId
        return try {
            val user = userService.findByIdOrNull(uuid ?: throw AmttdException(AmttdException.ErrorCode.INVALID_JSON))
                ?: throw AmttdException(AmttdException.ErrorCode.USER_NOT_FOUND)
            user.name = newUsername
            userService.saveImmediately(user)
            SimpleResponse(success = true)
        } catch (e: Exception) {
            SimpleResponse(success = false, error = AmttdException.getFromException(e).errorCodeValue)
        }
    }
}