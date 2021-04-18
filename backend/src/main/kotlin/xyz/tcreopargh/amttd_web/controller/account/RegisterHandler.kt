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
import xyz.tcreopargh.amttd_web.util.printlnAndClose
import xyz.tcreopargh.amttd_web.util.readAndClose
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
class RegisterHandler : AuthenticationController() {
    @RequestMapping("/register", method = [RequestMethod.POST])
    fun resolveRegister(request: HttpServletRequest, response: HttpServletResponse) {
        val body = request.reader.readAndClose()
        var jsonResponse = jsonObjectOf(
            "success" to false,
            "reason" to State.UNKNOWN.name
        )
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

            jsonResponse =
                jsonObjectOf(
                    "success" to true,
                    "username" to (user.name ?: ""),
                    "uuid" to user.uuid,
                    "token" to generatedToken.token
                )


        } catch (e: AuthenticationException) {
            jsonResponse =
                jsonObjectOf(
                    "success" to false,
                    "reason" to e.state.name
                )

        } finally {
            response.writer.printlnAndClose(jsonResponse.toString())
        }
    }
}