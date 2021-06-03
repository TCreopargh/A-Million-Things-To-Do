package xyz.tcreopargh.amttd_web.controller.account

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import xyz.tcreopargh.amttd_web.common.bean.request.LoginRequest
import xyz.tcreopargh.amttd_web.common.bean.response.LoginResponse
import xyz.tcreopargh.amttd_web.common.exception.AmttdException
import xyz.tcreopargh.amttd_web.common.exception.AmttdException.ErrorCode
import xyz.tcreopargh.amttd_web.component.AuthenticationInterceptor
import xyz.tcreopargh.amttd_web.controller.ControllerBase
import xyz.tcreopargh.amttd_web.entity.EntityAuthToken
import xyz.tcreopargh.amttd_web.entity.EntityUser
import xyz.tcreopargh.amttd_web.util.logger
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @author TCreopargh
 *
 * Handle user login.
 *
 * Login can be performed in 2 ways: email+password or uuid+token
 */
@RestController
class LoginController : ControllerBase() {
    @PostMapping(
        "/login",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun resolveLogin(
        request: HttpServletRequest,
        httpResponse: HttpServletResponse,
        @RequestBody loginBody: LoginRequest
    ): LoginResponse {
        val response: LoginResponse
        try {
            AuthenticationInterceptor.resetSession(request, httpResponse)
            val password = loginBody.password
            val email = loginBody.email?.lowercase()
            val token = loginBody.token
            val uuid = loginBody.uuid

            if (!((password != null && email != null) || (token != null && uuid != null))) {
                throw AmttdException(ErrorCode.FIELD_MISSING)
            }

            if (token != null) {
                //Token is present, attempt login with token
                val tokenFound = tokenService.findByToken(token)
                if (tokenFound == null) {
                    logger.info("Token not found!  $token")
                    throw AmttdException(ErrorCode.INVALID_TOKEN)
                } else if (tokenFound.isExpired()) {
                    logger.info("Token expired!  $token")
                    tokenService.remove(tokenFound)
                    throw AmttdException(ErrorCode.INVALID_TOKEN)
                } else {
                    val tokenUser = tokenFound.user ?: throw AmttdException(ErrorCode.USER_NOT_FOUND)
                    if (tokenUser.uuid == uuid) {
                        response = LoginResponse(
                            success = true,
                            email = tokenUser.email,
                            username = (tokenUser.name ?: ""),
                            uuid = uuid,
                            token = token
                        )
                    } else {
                        logger.info("Token does not match the current user!  Token: $token  Expected UUID: ${tokenUser.uuid}  Actual UUID: $uuid")
                        throw AmttdException(ErrorCode.INVALID_TOKEN)
                    }
                }

            } else {

                if (!isEmailValid(email)) {
                    throw AmttdException(ErrorCode.ILLEGAL_EMAIL)
                }
                if (!isPasswordValid(password)) {
                    throw AmttdException(ErrorCode.ILLEGAL_PASSWORD)
                }
                val users: List<EntityUser> = userService.findByEmail(email ?: "")
                val user = users.getOrNull(0) ?: throw AmttdException(ErrorCode.USER_NOT_FOUND)
                var generatedToken = EntityAuthToken(user)
                generatedToken = tokenService.saveImmediately(generatedToken)
                if (user.password == password && user.email == email) {
                    response = LoginResponse(
                        success = true,
                        email = user.email,
                        username = user.name,
                        uuid = user.uuid,
                        token = generatedToken.token
                    )
                } else {
                    throw AmttdException(ErrorCode.INCORRECT_PASSWORD)
                }
            }
            tokenService.removeExpired()
            if (response.success == true) {
                request.session.setAttribute("uuid", response.uuid.toString())
                request.session.setAttribute("token", response.token)
            }
            return response
        } catch (e: AmttdException) {
            logger.error("Authentication error! ", e)
            return LoginResponse(
                success = false,
                error = e.errorCodeValue
            )
        }
    }
}