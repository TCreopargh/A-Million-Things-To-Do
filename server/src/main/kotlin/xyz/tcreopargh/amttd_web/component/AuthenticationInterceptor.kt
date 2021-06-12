package xyz.tcreopargh.amttd_web.component

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import xyz.tcreopargh.amttd_web.annotation.LoginRequired
import xyz.tcreopargh.amttd_web.entity.EntityUser
import xyz.tcreopargh.amttd_web.service.TokenService
import xyz.tcreopargh.amttd_web.service.UserService
import java.io.IOException
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.reflect.KFunction
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.jvm.kotlinFunction

/**
 * @author TCreopargh
 *
 * Interceptor class
 *
 * Mainly used to prevent actions without logging in
 */
@Component
class AuthenticationInterceptor : HandlerInterceptor {

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var tokenService: TokenService

    @Throws(IOException::class)
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (handler is HandlerMethod) {
            val method: KFunction<*>? = handler.method.kotlinFunction
            var isLoginRequired: Boolean = method?.hasAnnotation<LoginRequired>() ?: false
            val uuid = request.session.getAttribute("uuid")?.toString()
            val token = request.session.getAttribute("token")?.toString()
            if (!isLoginRequired) {
                isLoginRequired = method?.javaClass?.kotlin?.hasAnnotation<LoginRequired>() ?: false
            }
            if (isLoginRequired) {
                val uuidObject = try {
                    token!!
                    UUID.fromString(uuid)
                } catch (e: Exception) {
                    return requireLogin(request, response)
                }
                findUserByIdAndToken(uuidObject, token) ?: return requireLogin(request, response)
            }
        }
        return true
    }

    private fun findUserByIdAndToken(uuid: UUID, token: String): EntityUser? {
        var user = userService.findByIdOrNull(uuid)
        if (!tokenService.verify(token, uuid)) {
            user = null
        }
        return user
    }

    companion object {

        fun requireLogin(request: HttpServletRequest, response: HttpServletResponse): Boolean {
            val ret = resetSession(request)
            response.status = HttpStatus.FORBIDDEN.value()
            response.sendError(HttpStatus.FORBIDDEN.value())
            return ret
        }

        fun resetSession(request: HttpServletRequest): Boolean {
            request.session.apply {
                setAttribute("uuid", null)
                setAttribute("token", null)
            }
            request.session.invalidate()
            return false
        }
    }
}
