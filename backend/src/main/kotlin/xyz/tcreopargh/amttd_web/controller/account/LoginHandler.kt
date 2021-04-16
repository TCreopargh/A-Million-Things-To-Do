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
import xyz.tcreopargh.amttd_web.util.*
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
class LoginHandler : AuthenticationController() {
    @RequestMapping("/login", method = [RequestMethod.POST])
    fun resolveLogin(request: HttpServletRequest, response: HttpServletResponse) {
        val body = request.reader.readAndClose()
        var jsonResponse = jsonObjectOf()
        try {
            val jsonObject: JsonObject = try {
                JsonParser.parseString(body) as? JsonObject ?: throw JsonParseException("Json is empty!")
            } catch (e: JsonParseException) {
                throw LoginFailedException(State.CORRUPTED_DATA)
            }
            val password = jsonObject.get("password")?.asString
            val username = jsonObject.get("username")?.asString

            if (!isUserNameValid(username)) {
                throw LoginFailedException(State.ILLEGAL_USERNAME)
            }
            if (!isPasswordValid(username)) {
                throw LoginFailedException(State.ILLEGAL_PASSWORD)
            }
            logger.info("Received login JSON: $body")
            val generatedToken = random.nextString(128)
            val users: List<EntityUser> = userService.findByUsername(username ?: "")
            var loggedInUser: EntityUser? = null
            for (user in users) {
                if (user.password == password && user.name == username) {
                    loggedInUser = user
                    jsonResponse = jsonObjectOf(
                        "success" to true,
                        "username" to (user.name ?: ""),
                        "uuid" to UUID.randomUUID(),
                        "token" to generatedToken
                    )
                    break
                }
            }
            if (loggedInUser == null) {
                throw LoginFailedException(State.USER_NOT_FOUND)
            }
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