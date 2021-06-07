package xyz.tcreopargh.amttd_web.controller.account

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import xyz.tcreopargh.amttd_web.annotation.LoginRequired
import xyz.tcreopargh.amttd_web.api.exception.AmttdException
import xyz.tcreopargh.amttd_web.api.json.request.UserChangeAvatarRequest
import xyz.tcreopargh.amttd_web.api.json.request.UserProfileChangeRequest
import xyz.tcreopargh.amttd_web.api.json.response.SimpleResponse
import xyz.tcreopargh.amttd_web.controller.ControllerBase
import xyz.tcreopargh.amttd_web.entity.EntityUserAvatar
import xyz.tcreopargh.amttd_web.util.logger
import java.util.*
import javax.servlet.http.HttpServletRequest


/**
 * @author TCreopargh
 *
 * This class handles user profile changes.
 *
 * Not to be confused with [xyz.tcreopargh.amttd_web.controller.workgroup.WorkGroupUserController]
 * which manages users in a workgroup.
 */
@RestController
@LoginRequired
@RequestMapping("/user")
class UserController : ControllerBase() {
    @PostMapping(
        "/change-avatar",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun changeAvatar(request: HttpServletRequest, @RequestBody body: UserChangeAvatarRequest): SimpleResponse {
        return try {
            val imgBytes = body.img
            val user = verifyUser(request, body.userId)
            val avatar = EntityUserAvatar(
                avatarId = UUID.randomUUID(),
                user = user,
                image = imgBytes
            )
            user.avatar = avatar
            userService.saveImmediately(user)

            SimpleResponse(success = true)
        } catch (e: Exception) {
            logger.error("Exception in UserController: ", e)
            SimpleResponse(success = false, error = AmttdException.getFromException(e).errorCodeValue)
        }
    }

    @GetMapping(
        "/avatar/{uuid}"
    )
    fun getAvatar(request: HttpServletRequest, @PathVariable uuid: UUID): ResponseEntity<ByteArray> {
        val headers = HttpHeaders()
        headers.contentType = MediaType.IMAGE_PNG
        val user = userService.findByIdOrNull(uuid)
            ?: throw AmttdException(AmttdException.ErrorCode.REQUESTED_ENTITY_NOT_FOUND)
        return ResponseEntity<ByteArray>(user.avatar?.image, headers, HttpStatus.OK)
    }

    @PostMapping(
        "/change-profile",
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
            val user = verifyUser(request, uuid)
            if (newUsername != null && !isUsernameValid(newUsername)) {
                throw AmttdException(AmttdException.ErrorCode.ILLEGAL_USERNAME)
            }
            if (newEmail != null && !isEmailValid(newEmail)) {
                throw AmttdException(AmttdException.ErrorCode.ILLEGAL_EMAIL)
            }
            if (newPassword != null && !isPasswordValid(newPassword)) {
                throw AmttdException(AmttdException.ErrorCode.ILLEGAL_PASSWORD)
            }
            if (newEmail != null && userService.findByEmail(newEmail).isNotEmpty()) {
                throw AmttdException(AmttdException.ErrorCode.USER_ALREADY_EXISTS)
            }
            // Invalidate tokens
            if (newEmail != null || newPassword != null) {
                tokenService.findByUser(user).forEach {
                    tokenService.remove(it)
                }
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