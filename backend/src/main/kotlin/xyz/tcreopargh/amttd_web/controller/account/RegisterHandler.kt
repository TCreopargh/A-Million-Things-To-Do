package xyz.tcreopargh.amttd_web.controller.account

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import xyz.tcreopargh.amttd_web.binding.RegisterBody
import xyz.tcreopargh.amttd_web.controller.ControllerBase
import xyz.tcreopargh.amttd_web.entity.AuthToken
import xyz.tcreopargh.amttd_web.entity.EntityUser
import xyz.tcreopargh.amttd_web.exception.AuthenticationException
import xyz.tcreopargh.amttd_web.exception.AuthenticationException.State
import xyz.tcreopargh.amttd_web.exception.RegisterFailedException
import xyz.tcreopargh.amttd_web.util.jsonObjectOf
import javax.servlet.http.HttpServletRequest

@RestController
class RegisterHandler : ControllerBase() {
    @RequestMapping("/register", method = [RequestMethod.POST], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun resolveRegister(request: HttpServletRequest, @RequestBody registerBody: RegisterBody): String {
        try {
            val password = registerBody.password
            val username = registerBody.username

            if (!isUserNameValid(username)) {
                throw RegisterFailedException(State.ILLEGAL_USERNAME)
            }
            if (!isPasswordValid(password)) {
                throw RegisterFailedException(State.ILLEGAL_PASSWORD)
            }

            if (userService.findByUsername(username ?: "").isNotEmpty()) {
                throw RegisterFailedException(State.USER_ALREADY_EXISTS)
            }

            var user = EntityUser(
                name = username,
                password = password
            )
            user = userService.saveImmediately(user)
            var generatedToken = AuthToken(user)
            generatedToken = tokenService.saveImmediately(generatedToken)

            request.session.setAttribute("uuid", user.uuid.toString())
            request.session.setAttribute("token", generatedToken.token)

            return jsonObjectOf(
                "success" to true,
                "username" to (user.name ?: ""),
                "uuid" to user.uuid,
                "token" to generatedToken.token
            ).toString()
        } catch (e: AuthenticationException) {
            return jsonObjectOf(
                "success" to false,
                "reason" to e.state.name
            ).toString()
        }
    }
}