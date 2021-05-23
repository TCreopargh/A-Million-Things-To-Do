package xyz.tcreopargh.amttd_web.controller.account

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import xyz.tcreopargh.amttd_web.annotation.LoginRequired
import xyz.tcreopargh.amttd_web.common.bean.request.UserProfileChangeRequest
import xyz.tcreopargh.amttd_web.common.bean.response.SimpleResponse
import xyz.tcreopargh.amttd_web.common.exception.AmttdException
import xyz.tcreopargh.amttd_web.controller.ControllerBase
import xyz.tcreopargh.amttd_web.util.logger
import javax.servlet.http.HttpServletRequest

@RestController
@LoginRequired
class UserController : ControllerBase() {
    @PostMapping(
        "/user/change-profile",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun changeProfile(request: HttpServletRequest, @RequestBody body: UserProfileChangeRequest): SimpleResponse {
        val newUsername = body.newUsername
        val newEmail = body.newEmail
        val newPassword = body.newPassword
        val uuid = body.userId
        return try {
            verifyUser(request, uuid)
            if (newUsername != null && !isUsernameValid(newUsername)) {
                throw AmttdException(AmttdException.ErrorCode.ILLEGAL_USERNAME)
            }
            if (newEmail != null && !isEmailValid(newEmail)) {
                throw AmttdException(AmttdException.ErrorCode.ILLEGAL_EMAIL)
            }
            if (newPassword != null && !isPasswordValid(newPassword)) {
                throw AmttdException(AmttdException.ErrorCode.ILLEGAL_PASSWORD)
            }
            val user = userService.findByIdOrNull(uuid ?: throw AmttdException(AmttdException.ErrorCode.INVALID_JSON))
                ?: throw AmttdException(AmttdException.ErrorCode.USER_NOT_FOUND)
            if (newEmail != null && userService.findByEmail(newEmail).isNotEmpty()) {
                throw AmttdException(AmttdException.ErrorCode.USER_ALREADY_EXISTS)
            }
            newUsername?.let { user.name = it }
            newEmail?.let { user.emailAddress = it }
            newPassword?.let { user.password = it }
            userService.saveImmediately(user)
            SimpleResponse(success = true)
        } catch (e: Exception) {
            logger.error("Exception in UserController: ", e)
            SimpleResponse(success = false, error = AmttdException.getFromException(e).errorCodeValue)
        }
    }
}