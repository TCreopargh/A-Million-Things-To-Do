package xyz.tcreopargh.amttd_web.controller.account

import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import xyz.tcreopargh.amttd_web.entity.AuthToken
import xyz.tcreopargh.amttd_web.entity.EntityUser
import xyz.tcreopargh.amttd_web.exception.AuthenticationException
import xyz.tcreopargh.amttd_web.exception.AuthenticationException.State
import xyz.tcreopargh.amttd_web.exception.RegisterFailedException
import xyz.tcreopargh.amttd_web.util.jsonObjectOf
import xyz.tcreopargh.amttd_web.util.logger
import xyz.tcreopargh.amttd_web.util.readAndClose
import javax.servlet.http.HttpServletRequest

@RestController
class RegisterHandler : AuthenticationController() {
    @RequestMapping("/register", method = [RequestMethod.POST])
    fun resolveRegister(request: HttpServletRequest): String {
        val body = request.reader.readAndClose()
        try {
            val jsonObject: JsonObject = try {
                JsonParser.parseString(body) as? JsonObject ?: throw JsonParseException("Json is empty!")
            } catch (e: JsonParseException) {
                throw RegisterFailedException(State.CORRUPTED_DATA)
            }
            val password = jsonObject.get("password")?.asString
            val username = jsonObject.get("username")?.asString

            if (!isUserNameValid(username)) {
                throw RegisterFailedException(State.ILLEGAL_USERNAME)
            }
            if (!isPasswordValid(password)) {
                throw RegisterFailedException(State.ILLEGAL_PASSWORD)
            }
            logger.info("Received register JSON: $body")

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