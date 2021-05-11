package xyz.tcreopargh.amttd_web.controller.account

import com.google.gson.JsonObject
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import xyz.tcreopargh.amttd_web.binding.LoginBody
import xyz.tcreopargh.amttd_web.controller.ControllerBase
import xyz.tcreopargh.amttd_web.entity.EntityAuthToken
import xyz.tcreopargh.amttd_web.entity.EntityUser
import xyz.tcreopargh.amttd_web.exception.AuthenticationException
import xyz.tcreopargh.amttd_web.exception.AuthenticationException.State
import xyz.tcreopargh.amttd_web.exception.LoginFailedException
import xyz.tcreopargh.amttd_web.util.jsonObjectOf
import xyz.tcreopargh.amttd_web.util.logger
import javax.servlet.http.HttpServletRequest

@RestController
class LoginHandler : ControllerBase() {
    @RequestMapping("/login", method = [RequestMethod.POST], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun resolveLogin(request: HttpServletRequest, @RequestBody loginBody: LoginBody): String {
        val jsonResponse: JsonObject
        try {
            val password = loginBody.password
            val username = loginBody.username
            val token = loginBody.token
            val uuid = loginBody.uuid
            if (token != null) {
                //Token is present, attempt login with token
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
                            "uuid" to uuid.toString(),
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
                var generatedToken = EntityAuthToken(user)
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
            if (jsonResponse.get("success")?.asBoolean == true) {
                request.session.setAttribute("uuid", jsonResponse.get("uuid").asString)
                request.session.setAttribute("token", jsonResponse.get("token").asString)
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