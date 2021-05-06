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
import xyz.tcreopargh.amttd_web.exception.LoginFailedException
import xyz.tcreopargh.amttd_web.util.jsonObjectOf
import xyz.tcreopargh.amttd_web.util.logger
import xyz.tcreopargh.amttd_web.util.readAndClose
import java.util.*
import javax.servlet.http.HttpServletRequest

@RestController
class LoginHandler : AuthenticationController() {
    @RequestMapping("/login", method = [RequestMethod.POST])
    fun resolveLogin(request: HttpServletRequest): String {
        val body = request.reader.readAndClose()
        val jsonResponse: JsonObject
        try {
            val jsonObject: JsonObject = try {
                JsonParser.parseString(body) as? JsonObject ?: throw JsonParseException("Json is empty!")
            } catch (e: JsonParseException) {
                throw LoginFailedException(State.CORRUPTED_DATA)
            }
            val password = jsonObject.get("password")?.asString
            val username = jsonObject.get("username")?.asString

            val token = jsonObject.get("token")?.asString
            logger.info("Received login JSON: $body")

            if (token != null) {
                //Token is present, attempt login with token
                val uuidString = jsonObject.get("uuid")?.asString
                val uuid = try {
                    UUID.fromString(uuidString)
                } catch (e: IllegalArgumentException) {
                    logger.info("User not found!")
                    throw LoginFailedException(State.USER_NOT_FOUND)
                }
                val tokenFound = tokenService.findByToken(token)
                if (tokenFound == null) {
                    logger.info("Token not found!  $token")
                    throw LoginFailedException(State.INVALID_TOKEN)
                } else if (tokenFound.isExpired()) {
                    logger.info("Token expired!  $token")
                    tokenService.remove(tokenFound)
                    throw LoginFailedException(State.INVALID_TOKEN)
                } else {
                    val tokenUser = tokenFound.user
                    if (tokenUser?.uuid == uuid) {
                        jsonResponse = jsonObjectOf(
                            "success" to true,
                            "username" to (tokenUser?.name ?: ""),
                            "uuid" to UUID.randomUUID(),
                            "token" to token
                        )
                    } else {
                        logger.info("Token does not match the current user!  Token: $token  Expected UUID: ${tokenUser?.uuid}  Actual UUID: $uuid")
                        throw LoginFailedException(State.INVALID_TOKEN)
                    }
                }

            } else {

                if (!isUserNameValid(username)) {
                    throw LoginFailedException(State.ILLEGAL_USERNAME)
                }
                if (!isPasswordValid(password)) {
                    throw LoginFailedException(State.ILLEGAL_PASSWORD)
                }
                val users: List<EntityUser> = userService.findByUsername(username ?: "")
                val user = users.getOrNull(0)
                var generatedToken = AuthToken(user)
                generatedToken = tokenService.saveImmediately(generatedToken)

                if (user?.password == password && user?.name == username) {
                    jsonResponse = jsonObjectOf(
                        "success" to true,
                        "username" to (user?.name ?: ""),
                        "uuid" to user?.uuid.toString(),
                        "token" to generatedToken.token
                    )
                } else {
                    throw LoginFailedException(State.INCORRECT_PASSWORD)
                }
            }
            return jsonResponse.toString()
        } catch (e: AuthenticationException) {
            return jsonObjectOf(
                "success" to false,
                "reason" to e.state.name
            ).toString()
        }
    }
}