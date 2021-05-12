package xyz.tcreopargh.amttd_web.controller.account

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import xyz.tcreopargh.amttd_web.bean.RegisterBody
import xyz.tcreopargh.amttd_web.controller.ControllerBase
import xyz.tcreopargh.amttd_web.entity.EntityAuthToken
import xyz.tcreopargh.amttd_web.entity.EntityUser
import xyz.tcreopargh.amttd_web.exception.AuthenticationException
import xyz.tcreopargh.amttd_web.exception.AuthenticationException.State
import xyz.tcreopargh.amttd_web.exception.RegisterFailedException
import xyz.tcreopargh.amttd_web.util.jsonObjectOf
import xyz.tcreopargh.amttd_web.util.nextString
import xyz.tcreopargh.amttd_web.util.random
import javax.servlet.http.HttpServletRequest

@RestController
class RegisterHandler : ControllerBase() {
    @RequestMapping("/register", method = [RequestMethod.POST], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun resolveRegister(request: HttpServletRequest, @RequestBody registerBody: RegisterBody): String {
        try {
            val password = registerBody.password
            val email = registerBody.email?.lowercase()
            var username = registerBody.username

            if (!isEmailValid(email)) {
                throw RegisterFailedException(State.ILLEGAL_EMAIL)
            }
            if (!isPasswordValid(password)) {
                throw RegisterFailedException(State.ILLEGAL_PASSWORD)
            }

            if (userService.findByEmail(email ?: "").isNotEmpty()) {
                throw RegisterFailedException(State.USER_ALREADY_EXISTS)
            }

            if (email == null || password == null) {
                throw RegisterFailedException(State.FIELD_MISSING)
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

            return jsonObjectOf(
                "success" to true,
                "email" to user.email,
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