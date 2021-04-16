package xyz.tcreopargh.amttd_web.controller.account

import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import xyz.tcreopargh.amttd_web.entity.EntityUser
import xyz.tcreopargh.amttd_web.exception.AuthenticationException
import xyz.tcreopargh.amttd_web.exception.AuthenticationException.State
import xyz.tcreopargh.amttd_web.exception.LoginFailedException
import xyz.tcreopargh.amttd_web.exception.RegisterFailedException
import xyz.tcreopargh.amttd_web.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
class RegisterHandler : AuthenticationController() {
    @RequestMapping("/register", method = [RequestMethod.POST])
    fun resolveRegister(request: HttpServletRequest, response: HttpServletResponse) {
        val body = request.reader.readAndClose()
        var jsonResponse = jsonObjectOf()
        try {
            val jsonObject: JsonObject = try {
                JsonParser.parseString(body) as? JsonObject ?: throw JsonParseException("Json is empty!")
            } catch (e: JsonParseException) {
                throw RegisterFailedException(State.CORRUPTED_DATA)
            }
            val password = jsonObject.get("password")?.asString
            val username = jsonObject.get("username")?.asString
            val generatedToken = random.nextString(128)
            logger.info("Received register JSON: $body")

            if (!isUserNameValid(username)) {
                throw RegisterFailedException(State.ILLEGAL_USERNAME)
            }
            if (!isPasswordValid(username)) {
                throw RegisterFailedException(State.ILLEGAL_PASSWORD)
            }

            var user = EntityUser(
                name = username,
                password = password
            )
            user = userService.save(user)

            jsonResponse =
                jsonObjectOf(
                    "success" to true,
                    "username" to (user.name ?: ""),
                    "uuid" to user.uuid,
                    "token" to generatedToken
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