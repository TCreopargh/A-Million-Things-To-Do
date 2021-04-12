package xyz.tcreopargh.amttd_web.account

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import xyz.tcreopargh.amttd_web.util.jsonObjectOf
import xyz.tcreopargh.amttd_web.util.nextString
import xyz.tcreopargh.amttd_web.util.printlnAndClose
import xyz.tcreopargh.amttd_web.util.random
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
class LoginHandler {
    @RequestMapping("/login", method = [RequestMethod.POST])
    fun resolveLogin(request: HttpServletRequest, response: HttpServletResponse): String {
        val password = request.getParameter("password")
        val username = request.getParameter("username")
        val jsonResponse = if (password == "123456" && username == "hello") {
            jsonObjectOf(
                "success" to true,
                "username" to username,
                "uuid" to UUID.randomUUID(),
                "token" to random.nextString(128)
            )
        } else {
            jsonObjectOf(
                "success" to false,
                "reason" to "Password Incorrect"
            )
        }
        response.writer.printlnAndClose(jsonResponse.toString())
        return jsonResponse.toString()
    }
}