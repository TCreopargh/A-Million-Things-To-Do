package xyz.tcreopargh.amttd_web.controller.account

import com.google.gson.JsonObject
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import xyz.tcreopargh.amttd_web.bean.request.LoginRequest
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
    @PostMapping("/login", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun resolveLogin(request: HttpServletRequest, @RequestBody loginBody: LoginRequest): String {
        val jsonResponse: JsonObject
        try {
            val password = loginBody.password
            val email = loginBody.email?.lowercase()
            val token = loginBody.token
            val uuid = loginBody.uuid

            if (!((password != null && email != null) || (token != null && uuid != null))) {
                throw LoginFailedException(State.FIELD_MISSING)
            }

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
                    val tokenUser = tokenFound.user ?: throw LoginFailedException(State.USER_NOT_FOUND)
                    if (tokenUser.uuid == uuid) {
                        jsonResponse = jsonObjectOf(
                            "success" to true,
                            "email" to tokenUser.email,
                            "username" to (tokenUser.name ?: ""),
                            "uuid" to uuid.toString(),
                            "token" to token
                        )
                    } else {
                        logger.info("Token does not match the current user!  Token: $token  Expected UUID: ${tokenUser.uuid}  Actual UUID: $uuid")
                        throw LoginFailedException(State.INVALID_TOKEN)
                    }
                }

            } else {

                if (!isEmailValid(email)) {
                    throw LoginFailedException(State.ILLEGAL_EMAIL)
                }
                if (!isPasswordValid(password)) {
                    throw LoginFailedException(State.ILLEGAL_PASSWORD)
                }
                val users: List<EntityUser> = userService.findByEmail(email ?: "")
                val user = users.getOrNull(0) ?: throw LoginFailedException(State.USER_NOT_FOUND)
                var generatedToken = EntityAuthToken(user)
                generatedToken = tokenService.saveImmediately(generatedToken)
                if (user.password == password && user.email == email) {
                    jsonResponse = jsonObjectOf(
                        "success" to true,
                        "email" to user.email,
                        "username" to (user.name ?: ""),
                        "uuid" to user.uuid.toString(),
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
                "reason" to e.state.toString()
            ).toString()
        }
    }
}