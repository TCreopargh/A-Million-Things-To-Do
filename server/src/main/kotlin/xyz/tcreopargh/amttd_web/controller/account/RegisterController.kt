package xyz.tcreopargh.amttd_web.controller.account

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import xyz.tcreopargh.amttd_web.common.bean.request.RegisterRequest
import xyz.tcreopargh.amttd_web.common.bean.response.LoginResponse
import xyz.tcreopargh.amttd_web.common.exception.AmttdException
import xyz.tcreopargh.amttd_web.common.exception.AmttdException.ErrorCode
import xyz.tcreopargh.amttd_web.component.AuthenticationInterceptor
import xyz.tcreopargh.amttd_web.controller.ControllerBase
import xyz.tcreopargh.amttd_web.entity.EntityAuthToken
import xyz.tcreopargh.amttd_web.entity.EntityUser
import xyz.tcreopargh.amttd_web.util.logger
import xyz.tcreopargh.amttd_web.util.nextString
import xyz.tcreopargh.amttd_web.util.random
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @author TCreopargh
 *
 * Handle user register.
 */
@RestController
class RegisterController : ControllerBase() {
    @PostMapping(
        "/register",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun resolveRegister(
        request: HttpServletRequest,
        httpResponse: HttpServletResponse,
        @RequestBody registerBody: RegisterRequest
    ): LoginResponse {
        try {
            AuthenticationInterceptor.resetSession(request, httpResponse)
            val password = registerBody.password
            val email = registerBody.email?.lowercase()
            var username = registerBody.username

            if (!isEmailValid(email)) {
                throw AmttdException(ErrorCode.ILLEGAL_EMAIL)
            }
            if (!isUsernameValid(username)) {
                throw AmttdException(ErrorCode.ILLEGAL_USERNAME)
            }
            if (!isPasswordValid(password)) {
                throw AmttdException(ErrorCode.ILLEGAL_PASSWORD)
            }

            if (userService.findByEmail(email ?: "").isNotEmpty()) {
                throw AmttdException(ErrorCode.USER_ALREADY_EXISTS)
            }

            if (email == null || password == null) {
                throw AmttdException(ErrorCode.FIELD_MISSING)
            }

            if (username == null) {
                username = (email.split("@").getOrNull(0) ?: random.nextString(random.nextInt(8, 15)))
                    .replace("[^a-zA-Z0-9_]".toRegex(), " ")
            }

            var user = EntityUser(
                name = username,
                emailAddress = email,
                password = password
            )
            user = userService.saveImmediately(user)
            var generatedToken = EntityAuthToken(user)
            generatedToken = tokenService.saveImmediately(generatedToken)

            request.session.setAttribute("uuid", user.uuid.toString())
            request.session.setAttribute("token", generatedToken.token)

            return LoginResponse(
                success = true,
                email = "user.email",
                username = user.name,
                uuid = user.uuid,
                token = generatedToken.token
            )
        } catch (e: AmttdException) {
            logger.error("Authentication error! ", e)
            return LoginResponse(
                success = false,
                error = e.errorCodeValue
            )
        }
    }
}